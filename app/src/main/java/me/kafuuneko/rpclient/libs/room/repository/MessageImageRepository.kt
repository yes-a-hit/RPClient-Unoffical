package me.kafuuneko.rpclient.libs.room.repository

import androidx.room.withTransaction
import me.kafuuneko.rpclient.libs.room.AppDatabase
import me.kafuuneko.rpclient.libs.room.entity.ChatMessage
import me.kafuuneko.rpclient.libs.room.entity.MessageImageEntity
import me.kafuuneko.rpclient.libs.room.model.MessageImageInput
import me.kafuuneko.rpclient.libs.room.model.MessageImagePolicy
import me.kafuuneko.rpclient.libs.room.model.MessageImageWithFile
import me.kafuuneko.rpclient.libs.room.model.MessageKey
import me.kafuuneko.rpclient.libs.room.model.MessageType
import me.kafuuneko.rpclient.libs.room.model.MessageWithImages
import me.kafuuneko.rpclient.libs.room.model.PreparedFile

/**
 * 消息附件的共享数据协调能力，由会话及角色仓库编排调用。
 * - 将文件提交、关系变更、消息变更纳入同一短事务。
 * - 附件能力不区分普通消息角色，父消息及会话归属在关系写入前验证。
 * - 聚合读取使用批次查询及一致快照，不改变消息分页和摘要边界。
 */
class MessageImageRepository(
    private val mDatabase: AppDatabase,
    private val mFiles: FileRepository
) {
    private val mImages = mDatabase.getMessageImageDao()
    private val mFileDao = mDatabase.getFileDao()

    /** 先持文件锁，再由文件仓库开启短事务；仅供业务仓库调用，禁止在已有事务内重新加锁。 */
    internal suspend fun <T> mutate(
        prepared: List<PreparedFile> = emptyList(),
        block: suspend FileRepository.Mutation.() -> T
    ): T = mFiles.mutate(prepared, block)

    /** 在所属消息事务内替换有序附件；既有 UUID 不允许跨消息挪用。 */
    internal suspend fun replaceInTransaction(
        mutation: FileRepository.Mutation,
        key: MessageKey,
        sessionId: Long,
        inputs: List<MessageImageInput>
    ) {
        require(inputs.size <= MessageImagePolicy.MAX_IMAGES_PER_MESSAGE) { "A message can contain at most four images" }
        validateOwner(key, sessionId)
        val old = mImages.getByMessage(key.messageType, key.messageId)
        val oldUuids = old.map { it.imageUuid }.toSet()
        // 新附件必须来自本事务已提交索引的草稿；保留附件必须属于原消息。
        // 图库挑图走的是第三种情况：文件早就在库里，这里为它建一份独立索引，
        // 字节仍按 hash 共享——直接复用原 uuid 会让两条消息共用一条索引，
        // 删掉其中一条就会把另一条的附图一起带走。
        val uuids = inputs.map {
            when (it) {
                is MessageImageInput.Existing ->
                    if (it.uuid in oldUuids) it.uuid
                    else mutation.copyReference(it.uuid).uuid

                is MessageImageInput.Prepared -> it.value.file.uuid
            }
        }
        require(uuids.distinct().size == uuids.size) { "The same image reference cannot be used twice" }
        mImages.deleteByMessage(key.messageType, key.messageId)
        mImages.insertAll(uuids.mapIndexed { position, uuid ->
            MessageImageEntity(key.messageType, key.messageId, position, uuid)
        })
        mutation.removeFiles((oldUuids - uuids.toSet()).toList())
    }

    /** 在删除父消息之前释放所选消息的附件；ID 集合必须由会话仓库限定范围。 */
    internal suspend fun deleteInTransaction(
        mutation: FileRepository.Mutation,
        type: MessageType,
        ids: List<Long>
    ) {
        ids.distinct().chunked(BATCH_SIZE).forEach { batch ->
            val uuids = mImages.getImageUuidsByMessages(type, batch)
            mImages.deleteByMessages(type, batch)
            mutation.removeFiles(uuids)
        }
    }

    /** 按分支消息 ID 映射复制附件，独立 UUID 共享原图 hash，保留原有 position。 */
    internal suspend fun copyInTransaction(
        mutation: FileRepository.Mutation,
        idMap: Map<Long, Long>,
        targetSessionId: Long
    ) {
        // 分支复制已有关系，保留导入消息的来源；每个附件仍创建独立引用。
        idMap.keys.toList().chunked(BATCH_SIZE).forEach { batch ->
            val images = mImages.getByMessages(MessageType.Single, batch)
            images.groupBy { it.messageId }.forEach { (sourceId, attachments) ->
                val targetId = idMap.getValue(sourceId)
                validateOwner(MessageKey(MessageType.Single, targetId), targetSessionId)
                mImages.insertAll(attachments.map {
                    it.copy(messageId = targetId, imageUuid = mutation.copyReference(it.imageUuid).uuid)
                })
            }
        }
    }

    /** 校验多态父消息与真实会话；摘要是独立维护的派生数据，不承载消息附件。 */
    private suspend fun validateOwner(key: MessageKey, sessionId: Long) {
        // 单聊与群聊使用独立 ID 空间，不能跨类型匹配父消息。
        when (key.messageType) {
            MessageType.Single -> {
                val message = requireNotNull(mDatabase.getChatMessageDao().getMessageById(key.messageId)) {
                    "The message owning the image does not exist"
                }
                require(message.sessionId == sessionId) { "The image owner belongs to a different session" }
                require(message.source != ChatMessage.Source.Summary) {
                    "Summary records cannot own message images"
                }
            }
            MessageType.Group -> {
                val message = requireNotNull(mDatabase.getGroupChatMessageDao().getMessageById(key.messageId)) {
                    "The message owning the image does not exist"
                }
                require(message.sessionId == sessionId) { "The image owner belongs to a different session" }
            }
        }
    }

    /** 在一致事务内按调用方消息顺序批量返回正文与附件；已删除消息不返回。 */
    suspend fun getMessages(keys: List<MessageKey>): List<MessageWithImages> = mDatabase.withTransaction {
        // 两种消息使用独立 ID 空间，不能合并为只有 Long 的映射。
        val result = mutableMapOf<MessageKey, MessageWithImages>()
        keys.distinct().groupBy { it.messageType }.forEach { (type, typedKeys) ->
            typedKeys.chunked(BATCH_SIZE).forEach { batch ->
                val ids = batch.map { it.messageId }
                val images = mImages.getByMessages(type, ids)
                val files = images.map { it.imageUuid }.chunked(BATCH_SIZE)
                    .flatMap { mFileDao.getByUuids(it) }.associateBy { it.uuid }
                val byMessage = images.groupBy { it.messageId }.mapValues { (_, attachments) ->
                    attachments.map { MessageImageWithFile(it, files[it.imageUuid]) }
                }
                // 正文也批量读取，避免长历史退化成逐消息／逐图片查库。
                when (type) {
                    MessageType.Single -> mDatabase.getChatMessageDao().getByIds(ids).forEach {
                        val key = MessageKey(type, it.id)
                        result[key] = MessageWithImages(key, it.content, it.source.name, it.createTime,
                            byMessage[it.id].orEmpty(), coveredMessageId = it.coveredMessageId)
                    }
                    MessageType.Group -> mDatabase.getGroupChatMessageDao().getByIds(ids).forEach {
                        val key = MessageKey(type, it.id)
                        result[key] = MessageWithImages(key, it.content, it.source.name, it.createTime,
                            byMessage[it.id].orEmpty(), it.speakerCharacterId, it.speakerNameSnapshot)
                    }
                }
            }
        }
        keys.mapNotNull { result[it] }
    }

    private companion object {
        const val BATCH_SIZE = 900
    }
}
