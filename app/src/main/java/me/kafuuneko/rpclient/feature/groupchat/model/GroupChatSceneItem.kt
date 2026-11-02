package me.kafuuneko.rpclient.feature.groupchat.model

/**
 * 群聊场景标签的展示数据。
 *
 * @property memberNames 驻扎在本场的角色名，用于标签上直接看出「这一场有谁」。
 * @property restrictMembers 是否限定了成员；false 表示全员都在。
 * @property lorebookEntryIds 本场限定的世界书条目；空表示沿用会话设置。
 * @property lorebookEntryCount 限定条目数，标签上直接显示「设定 2 条」。
 */
data class GroupChatSceneItem(
    val id: String,
    val name: String,
    val memberNames: List<String> = emptyList(),
    val restrictMembers: Boolean = false,
    val lorebookEntryIds: List<Long> = emptyList(),
    val lorebookEntryCount: Int = lorebookEntryIds.size
)
