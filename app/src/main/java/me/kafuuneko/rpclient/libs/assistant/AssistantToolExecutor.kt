package me.kafuuneko.rpclient.libs.assistant

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.kafuuneko.rpclient.libs.assistant.AssistantToolCatalog.longOrNull
import me.kafuuneko.rpclient.libs.assistant.AssistantToolCatalog.stringList
import me.kafuuneko.rpclient.libs.assistant.AssistantToolCatalog.stringOrNull
import me.kafuuneko.rpclient.libs.character.CharacterProfileCodec
import me.kafuuneko.rpclient.libs.character.CharacterProfileExtra
import me.kafuuneko.rpclient.libs.character.RelationshipStatus
import me.kafuuneko.rpclient.libs.room.entity.Character
import me.kafuuneko.rpclient.libs.room.entity.LorebookEntry
import me.kafuuneko.rpclient.libs.room.repository.CharacterRepository
import me.kafuuneko.rpclient.libs.room.repository.LorebookRepository
import me.kafuuneko.rpclient.libs.script.ScriptImportOutcome
import me.kafuuneko.rpclient.libs.script.ScriptPackCodec
import me.kafuuneko.rpclient.libs.gallery.GalleryStore
import me.kafuuneko.rpclient.libs.script.ScriptStore

/**
 * 助手工具执行器：在受限沙箱内完成文件操作与角色卡编辑。
 *
 * 权限边界只有两条：
 * - 文件路径经规范化后必须落在沙箱根目录内部，越界请求直接拒绝。
 * - 角色卡只能通过 Repository 修改白名单字段，删除角色与修改绑定关系不在授权范围内。
 *
 * 所有失败都转成可直接回填给模型的中文提示，不抛异常打断对话。
 */
class AssistantToolExecutor(
    private val mSandboxRoot: File,
    private val mCharacterRepository: CharacterRepository,
    private val mLorebookRepository: LorebookRepository,
    private val mScriptStore: ScriptStore,
    private val mGalleryStore: GalleryStore,
    /**
     * 角色扩展档案编解码器。
     *
     * 角色卡的「年龄 / 性别 / 感情经历 / 性经历 / 边界」这类自定义栏目不存在数据库列里，
     * 统一以 `rpclient_profile` 命名空间塞进 `extensionsJson`。助手要填这些栏目就必须
     * 复用与界面完全相同的编解码路径，否则两边写出来的 JSON 结构会漂移。
     */
    private val mProfileCodec: CharacterProfileCodec = CharacterProfileCodec(Gson())
) {

    /** 剧本编解码器，仅用于把已有剧本回填给模型查看。 */
    private val mScriptCodec = ScriptPackCodec()
    /** 执行一次调用，返回回填给模型的文本结果。 */
    suspend fun execute(call: AssistantToolCall): String = withContext(Dispatchers.IO) {
        runCatching {
            when (call.name) {
                "list_files" -> listFiles(call.arguments)
                "read_file" -> readFile(call.arguments)
                "write_file" -> writeFile(call.arguments)
                "move_file" -> moveFile(call.arguments)
                "delete_file" -> deleteFile(call.arguments)
                "list_characters" -> listCharacters()
                "get_character" -> getCharacter(call.arguments)
                "create_character" -> createCharacter(call.arguments)
                "update_character" -> updateCharacter(call.arguments)
                "list_worldbooks" -> listWorldbooks()
                "create_worldbook" -> createWorldbook(call.arguments)
                "add_worldbook_entry" -> addWorldbookEntry(call.arguments)
                "get_worldbook_entries" -> getWorldbookEntries(call.arguments)
                "list_scripts" -> listScripts()
            "list_gallery" -> listGallery()
            "send_image" -> sendImage(call.arguments)
            "add_image_label" -> addImageLabel(call.arguments)
                "get_script" -> getScript(call.arguments)
                "create_script" -> createScript(call.arguments)
                else -> "未知工具：${call.name}"
            }
        }.getOrElse { error ->
            "工具执行失败：${error.message ?: error::class.simpleName}"
        }
    }

    /** 列出沙箱目录；路径越界或不存在时返回说明文本。 */
    private fun listFiles(arguments: JsonObject): String {
        val target = resolveSandboxPath(arguments.stringOrNull("path") ?: ".") ?: return OUT_OF_SANDBOX
        if (!target.exists()) return "路径不存在：${relativePath(target)}"
        if (!target.isDirectory) return "不是目录：${relativePath(target)}"
        val entries = target.listFiles()?.sortedBy { it.name } ?: emptyList()
        if (entries.isEmpty()) return "目录为空：${relativePath(target)}"
        val lines = entries.map { entry ->
            val kind = if (entry.isDirectory) "dir " else "file"
            val size = if (entry.isDirectory) "-" else entry.length().toString()
            "$kind\t$size\t${entry.name}"
        }
        return "目录 ${relativePath(target)} 共 ${entries.size} 项：\n" + lines.joinToString("\n")
    }

    /** 读取文本文件；超长内容按上限截断并注明。 */
    private fun readFile(arguments: JsonObject): String {
        val path = arguments.stringOrNull("path") ?: return "缺少参数 path"
        val target = resolveSandboxPath(path) ?: return OUT_OF_SANDBOX
        if (!target.isFile) return "文件不存在：$path"
        if (target.length() > AssistantToolCatalog.MAX_FILE_BYTES) {
            return "文件过大（${target.length()} 字节），超过读取上限。"
        }
        val text = target.readText()
        if (text.length <= AssistantToolCatalog.MAX_READ_CHARS) return text
        return text.take(AssistantToolCatalog.MAX_READ_CHARS) +
            "\n…（已截断，原文件 ${text.length} 字符）"
    }

    /** 写入文本文件，自动创建父目录。 */
    private fun writeFile(arguments: JsonObject): String {
        val path = arguments.stringOrNull("path") ?: return "缺少参数 path"
        val content = arguments.stringOrNull("content") ?: return "缺少参数 content"
        if (content.toByteArray().size > AssistantToolCatalog.MAX_FILE_BYTES) {
            return "内容超过写入上限 ${AssistantToolCatalog.MAX_FILE_BYTES} 字节。"
        }
        val target = resolveSandboxPath(path) ?: return OUT_OF_SANDBOX
        if (target.isDirectory) return "目标是目录，无法写入：$path"
        target.parentFile?.mkdirs()
        target.writeText(content)
        return "已写入 ${relativePath(target)}（${content.length} 字符）"
    }

    /** 移动或重命名文件，源与目标都必须位于沙箱内。 */
    private fun moveFile(arguments: JsonObject): String {
        val from = arguments.stringOrNull("from") ?: return "缺少参数 from"
        val to = arguments.stringOrNull("to") ?: return "缺少参数 to"
        val source = resolveSandboxPath(from) ?: return "源路径越界：$from"
        val destination = resolveSandboxPath(to) ?: return "目标路径越界：$to"
        if (!source.exists()) return "源文件不存在：$from"
        if (destination.exists()) return "目标已存在，未覆盖：$to"
        destination.parentFile?.mkdirs()
        if (!source.renameTo(destination)) return "移动失败：$from -> $to"
        return "已移动 ${relativePath(source)} -> ${relativePath(destination)}"
    }

    /** 删除文件；目录不在此工具的授权范围内。 */
    private fun deleteFile(arguments: JsonObject): String {
        val path = arguments.stringOrNull("path") ?: return "缺少参数 path"
        val target = resolveSandboxPath(path) ?: return OUT_OF_SANDBOX
        if (!target.exists()) return "文件不存在：$path"
        if (target.isDirectory) return "该工具只删除文件，目录请逐个清理。"
        return if (target.delete()) "已删除 ${relativePath(target)}" else "删除失败：$path"
    }

    /** 列出全部角色卡的精简信息，供模型挑选要编辑的对象。 */
    private suspend fun listCharacters(): String {
        val characters = mCharacterRepository.getAllCharacters()
        if (characters.isEmpty()) return "当前没有任何角色卡。"
        val lines = characters.map { character ->
            "id=${character.id}\t${character.name}\t${describe(character)}"
        }
        return "共 ${characters.size} 张角色卡：\n" + lines.joinToString("\n")
    }

    /** 读取单张角色卡的完整字段。 */
    private suspend fun getCharacter(arguments: JsonObject): String {
        val id = arguments.longOrNull("id") ?: return "缺少参数 id"
        val character = mCharacterRepository.getCharacterById(id)
            ?: return "没有 id=$id 的角色卡"
        val profile = mProfileCodec.extract(character.extensionsJson)
        return listOf(
            "id=${character.id}",
            "name=${character.name}",
            "tags=${character.getCharacterTagList().joinToString("、")}",
            "description=${character.description}",
            "personality=${character.personality}",
            "scenario=${character.scenario}",
            "firstMessages=${character.firstMessages}",
            "postHistoryInstructions=${character.postHistoryInstructions}",
            "creatorNotes=${character.creatorNotes}",
            "systemPrompt=${character.systemPrompt}",
            "profile=" + describeProfile(profile)
        ).joinToString("\n")
    }

    /**
     * 把扩展档案渲染成一行可读文本，供模型确认哪些栏目已经填过。
     *
     * 空档案显式返回「未填写（全部栏目为空）」，避免模型把缺省当成「作者确认没有」。
     */
    private fun describeProfile(profile: CharacterProfileExtra): String {
        if (!profile.hasContent) return "未填写（全部栏目为空）"
        val parts = buildList {
            if (profile.age.isNotBlank()) add("age=${profile.age}")
            if (profile.gender.isNotBlank()) add("gender=${profile.gender}")
            if (profile.occupation.isNotBlank()) add("occupation=${profile.occupation}")
            if (profile.orientation.isNotBlank()) add("orientation=${profile.orientation}")
            if (profile.relationshipStatus != RelationshipStatus.Unspecified) {
                add("relationshipStatus=${profile.relationshipStatus.persistedValue}")
            }
            profile.hasRomanticHistory?.let { add("hasRomanticHistory=$it") }
            if (profile.romanticHistory.isNotBlank()) add("romanticHistory=${profile.romanticHistory}")
            profile.hasSexualHistory?.let { add("hasSexualHistory=$it") }
            if (profile.sexualHistory.isNotBlank()) add("sexualHistory=${profile.sexualHistory}")
            if (profile.boundaries.isNotBlank()) add("boundaries=${profile.boundaries}")
            if (profile.likes.isNotBlank()) add("likes=${profile.likes}")
            if (profile.dislikes.isNotBlank()) add("dislikes=${profile.dislikes}")
            if (profile.extraNotes.isNotBlank()) add("extraNotes=${profile.extraNotes}")
        }
        return parts.joinToString("；")
    }

    /**
     * 解析模型传来的扩展档案对象。
     *
     * 只接受非空字段，未出现的字段保持 `null` 以便调用方区分「本次没提」与「本次清空」。
     * 布尔字段同时接受 true/false 与 "yes"/"no"/"true"/"false" 字符串，
     * 因为多数模型更习惯把三态答案写成英文词而不是 JSON 布尔。
     */
    private fun parseProfile(raw: JsonObject): Map<String, Any?> = buildMap {
        raw.stringOrNull("age")?.let { put("age", it.trim()) }
        raw.stringOrNull("gender")?.let { put("gender", it.trim()) }
        raw.stringOrNull("occupation")?.let { put("occupation", it.trim()) }
        raw.stringOrNull("orientation")?.let { put("orientation", it.trim()) }
        raw.stringOrNull("relationshipStatus")?.let { put("relationshipStatus", it.trim()) }
        raw.triStateOrNull("hasRomanticHistory")?.let { put("hasRomanticHistory", it) }
        raw.stringOrNull("romanticHistory")?.let { put("romanticHistory", it.trim()) }
        raw.triStateOrNull("hasSexualHistory")?.let { put("hasSexualHistory", it) }
        raw.stringOrNull("sexualHistory")?.let { put("sexualHistory", it.trim()) }
        raw.stringOrNull("boundaries")?.let { put("boundaries", it.trim()) }
        raw.stringOrNull("likes")?.let { put("likes", it.trim()) }
        raw.stringOrNull("dislikes")?.let { put("dislikes", it.trim()) }
        raw.stringOrNull("extraNotes")?.let { put("extraNotes", it.trim()) }
    }

    /**
     * 把档案补丁合并进已有档案；补丁里没出现的字段一律保留原值。
     *
     * `Map<String, Any?>` 里的值要么是 String（文本栏目）要么是 Boolean?（三态栏目），
     * 空字符串代表「本次主动清空这一栏」。
     */
    private fun mergeProfile(
        base: CharacterProfileExtra,
        patch: Map<String, Any?>
    ): CharacterProfileExtra {
        fun text(key: String, fallback: String): String = patch[key] as? String ?: fallback
        fun flag(key: String, fallback: Boolean?): Boolean? =
            if (patch.containsKey(key)) patch[key] as? Boolean else fallback
        return CharacterProfileExtra(
            age = text("age", base.age),
            gender = text("gender", base.gender),
            occupation = text("occupation", base.occupation),
            orientation = text("orientation", base.orientation),
            relationshipStatus = (patch["relationshipStatus"] as? String)?.let {
                RelationshipStatus.fromPersistedValue(it)
            } ?: base.relationshipStatus,
            hasRomanticHistory = flag("hasRomanticHistory", base.hasRomanticHistory),
            romanticHistory = text("romanticHistory", base.romanticHistory),
            hasSexualHistory = flag("hasSexualHistory", base.hasSexualHistory),
            sexualHistory = text("sexualHistory", base.sexualHistory),
            boundaries = text("boundaries", base.boundaries),
            likes = text("likes", base.likes),
            dislikes = text("dislikes", base.dislikes),
            extraNotes = text("extraNotes", base.extraNotes)
        )
    }

    /** 列出已保存的剧本，含场景数量。 */
    private suspend fun listScripts(): String {
        val items = mScriptStore.list()
        if (items.isEmpty()) return "还没有剧本"
        return items.joinToString("\n") { "${it.id} · ${it.name} · ${it.sceneCount} 个场景" }
    }

    /** 读取指定剧本的完整 JSON，便于模型在其基础上改。 */
    private suspend fun getScript(arguments: JsonObject): String {
        val id = arguments.stringOrNull("id")?.trim().orEmpty()
        if (id.isBlank()) return "缺少参数 id"
        val pack = mScriptStore.load(id) ?: return "剧本不存在：$id"
        return mScriptCodec.encode(pack).take(MAX_SCRIPT_CHARS)
    }

    /**
     * 保存模型生成的剧本 JSON。
     *
     * 校验与落库都交给 ScriptStore：解析失败会返回具体原因，
     * 模型据此修正后重试，避免把半残剧本写进磁盘。
     */
    private suspend fun createScript(arguments: JsonObject): String {
        val json = arguments.stringOrNull("json")?.trim().orEmpty()
        if (json.isBlank()) return "缺少参数 json"
        return when (val outcome = mScriptStore.importFromText(json)) {
            is ScriptImportOutcome.Success ->
                "已保存剧本 ${outcome.packName}（${outcome.sceneCount} 个场景，id=${outcome.packId}）"

            is ScriptImportOutcome.Failure -> "剧本校验失败：${outcome.detail}"
        }
    }

    /** 新建一张角色卡。
     *
     * 只有名称是必填项；其余字段留空时按空字符串落库，之后可以用 update_character 继续补。
     * 头像留空表示使用应用默认头像。
     */
    private suspend fun createCharacter(arguments: JsonObject): String {
        val name = arguments.stringOrNull("name")?.trim().orEmpty()
        if (name.isBlank()) return "缺少参数 name"
        // 扩展档案（年龄/性别/感情史/性经历/边界……）走与界面相同的编解码路径
        val profilePatch = arguments.getAsJsonObject("profile")?.let(::parseProfile).orEmpty()
        val profile = mergeProfile(CharacterProfileExtra(), profilePatch)
        val character = Character(
            name = name,
            avatar = "",
            characterTags = "[]",
            description = arguments.stringOrNull("description")?.trim().orEmpty(),
            personality = arguments.stringOrNull("personality")?.trim().orEmpty(),
            scenario = arguments.stringOrNull("scenario")?.trim().orEmpty(),
            firstMessages = arguments.stringOrNull("firstMessage")?.trim().orEmpty(),
            examplesOfDialogue = arguments.stringOrNull("exampleDialogue")?.trim().orEmpty(),
            postHistoryInstructions = arguments.stringOrNull("postHistoryInstructions")?.trim().orEmpty(),
            extensionsJson = mProfileCodec.inject("{}", profile)
        )
        // 同名就停下，不动已有的那张卡。
        //
        // 之前的写法是「同名则合并」：新值非空就盖掉原字段。
        // 这等于让模型一句话就能改写用户精心写好的角色卡——
        // 而且模型并不知道自己在覆盖东西，它只以为在建新卡。
        //
        // 要改内容请用 update_character 明确指定 id 与字段，
        // 那是双方都知情的一次修改，不是建卡顺手带出来的副作用。
        val existing = runCatching {
            mCharacterRepository.getAllCharacters().firstOrNull { it.name == name }
        }.getOrNull()
        if (existing != null) {
            return buildString {
                append("已存在同名角色卡「")
                append(name)
                append("」（id=")
                append(existing.id)
                append("），本次未创建，也没有改动它。")
                append("如果要改这张卡，请用 update_character 指定 id=")
                append(existing.id)
                append(" 和 fields；如果要另建一个，请换个名字。")
            }
        }
        val id = mCharacterRepository.saveCharacter(character)
        val profileNote = if (profile.hasContent) {
            "，扩展档案已写入：${describeProfile(profile)}"
        } else {
            ""
        }
        return "已创建角色卡 id=$id（名称：$name$profileNote）"
    }

    /** 按白名单字段更新角色卡；未提供的字段保持原值。 */
    private suspend fun updateCharacter(arguments: JsonObject): String {
        val id = arguments.longOrNull("id") ?: return "缺少参数 id"
        val fields = arguments.get("fields")
        if (fields == null || !fields.isJsonObject) return "缺少对象参数 fields"
        val patch = fields.asJsonObject
        val current = mCharacterRepository.getCharacterById(id)
            ?: return "没有 id=$id 的角色卡"

        // 扩展档案按字段级合并：只改 fields.profile 里出现的栏目，其余保持原值。
        // 直接把整个 profile 覆盖会让模型一个字段没提就把别的栏目清空。
        val profilePatch = patch.getAsJsonObject("profile")?.let(::parseProfile).orEmpty()
        val currentProfile = mProfileCodec.extract(current.extensionsJson)
        val nextProfile = if (profilePatch.isEmpty()) {
            currentProfile
        } else {
            mergeProfile(currentProfile, profilePatch)
        }

        val updated = current.copy(
            name = patch.stringOrNull("name") ?: current.name,
            description = patch.stringOrNull("description") ?: current.description,
            personality = patch.stringOrNull("personality") ?: current.personality,
            scenario = patch.stringOrNull("scenario") ?: current.scenario,
            firstMessages = patch.stringOrNull("firstMessages") ?: current.firstMessages,
            postHistoryInstructions = patch.stringOrNull("postHistoryInstructions")
                ?: current.postHistoryInstructions,
            creatorNotes = patch.stringOrNull("creatorNotes") ?: current.creatorNotes,
            systemPrompt = patch.stringOrNull("systemPrompt") ?: current.systemPrompt,
            extensionsJson = if (profilePatch.isEmpty()) {
                current.extensionsJson
            } else {
                mProfileCodec.inject(current.extensionsJson, nextProfile)
            }
        )
        if (updated == current) return "没有需要修改的字段。"
        mCharacterRepository.updateCharacter(updated)
        val changed = changedFields(current, updated)
        return "已更新角色卡 id=$id，修改字段：${changed.joinToString(", ")}"
    }

    /** 计算实际发生变化的字段名，便于模型确认结果。 */
    private fun changedFields(before: Character, after: Character): List<String> = buildList {
        if (before.name != after.name) add("name")
        if (before.description != after.description) add("description")
        if (before.personality != after.personality) add("personality")
        if (before.scenario != after.scenario) add("scenario")
        if (before.firstMessages != after.firstMessages) add("firstMessages")
        if (before.postHistoryInstructions != after.postHistoryInstructions) add("postHistoryInstructions")
        if (before.creatorNotes != after.creatorNotes) add("creatorNotes")
        if (before.systemPrompt != after.systemPrompt) add("systemPrompt")
        if (before.extensionsJson != after.extensionsJson) add("profile")
    }

    /** 列出全部世界书及条目数量，供助手判断新建还是追加。 */
    private suspend fun listWorldbooks(): String {
        val lorebooks = mLorebookRepository.getAllLorebooks()
        if (lorebooks.isEmpty()) return "当前没有任何世界书。"
        val counts = mLorebookRepository.getLorebookEntryCounts()
        val lines = lorebooks.map { lorebook ->
            val count = counts[lorebook.id] ?: 0
            // 空书单独标出来：模型建完就忘、又建一本，最后留下满地空壳
            val mark = if (count == 0) "（空书，没有条目）" else ""
            "id=${lorebook.id}\t${lorebook.name}\t$count 条$mark"
        }
        return "共 ${lorebooks.size} 本世界书：\n" + lines.joinToString("\n") +
            "\n需要新建前先在这里找同名书；已经有就直接用它的 id 追加条目。"
    }

    /**
     * 新建世界书，同名时复用已有那本。
     *
     * 之前的写法是「调一次就建一本」，模型忘了 id 或者追加条目失败重试，
     * 就会连开好几本同名空书。现在按名字去重，重复调用只会拿到同一个 id。
     *
     * 同时支持一次带上 `entries`，把「建书 + 写条目」压成一次调用，
     * 少一轮往返就少一次中途失败、少一堆只建不填的空壳。
     */
    private suspend fun createWorldbook(arguments: JsonObject): String {
        val name = arguments.stringOrNull("name")?.trim().orEmpty()
        if (name.isBlank()) return "缺少参数 name"
        val existing = mLorebookRepository.getAllLorebooks().firstOrNull { it.name == name }
        val id: Long
        val head: String
        if (existing != null) {
            id = existing.id
            head = "已存在同名世界书「$name」（id=$id），本次未新建，直接使用这本。"
        } else {
            id = mLorebookRepository.createLorebook(name)
            head = "已创建世界书 id=$id，名称：$name"
        }
        val entries = arguments.get("entries")?.takeIf { it.isJsonArray }?.asJsonArray ?: JsonArray()
        if (entries.isEmpty()) {
            return head + "\n请接着用 add_worldbook_entry 往 id=$id 里写条目；没有条目的世界书不会生效。"
        }
        val written = mutableListOf<String>()
        val skipped = mutableListOf<String>()
        entries.forEach { element ->
            val item = element.takeIf { it.isJsonObject }?.asJsonObject ?: return@forEach
            val entryName = item.stringOrNull("name")?.trim().orEmpty()
            val content = item.stringOrNull("content")?.trim().orEmpty()
            if (entryName.isBlank() || content.isBlank()) {
                skipped += (entryName.ifBlank { "(未命名)" } + "：缺少 name 或 content")
                return@forEach
            }
            writeWorldbookEntry(id, entryName, item.stringList("keys"), content)
            written += entryName
        }
        return buildString {
            append(head)
            append("\n本次写入 ${written.size} 条：")
            append(written.joinToString("、").ifBlank { "无" })
            if (skipped.isNotEmpty()) {
                append("\n跳过 ${skipped.size} 条（参数不全）：")
                append(skipped.joinToString("；"))
            }
        }
    }

    /** 往世界书写入一条条目；没有关键词时按常驻条目处理。 */
    private suspend fun writeWorldbookEntry(
        worldbookId: Long,
        name: String,
        keys: List<String>,
        content: String
    ): Long {
        // 关键词先留空，写入后由仓库接口统一序列化，避免两处格式不一致
        val entry = LorebookEntry(
            lorebookId = worldbookId,
            name = name,
            keywords = "[]",
            secondaryKeywords = "[]",
            constant = keys.isEmpty(),
            order = DEFAULT_ENTRY_ORDER,
            depth = DEFAULT_ENTRY_DEPTH,
            category = "[]",
            content = content
        )
        val entryId = mLorebookRepository.saveEntry(entry)
        if (keys.isNotEmpty()) mLorebookRepository.updateEntryKeywords(entryId, keys)
        return entryId
    }

    /** 向指定世界书追加一条条目；没有关键词时写入常驻条目。 */
    private suspend fun addWorldbookEntry(arguments: JsonObject): String {
        val worldbookId = arguments.longOrNull("worldbookId") ?: return "缺少参数 worldbookId"
        val name = arguments.stringOrNull("name")?.trim().orEmpty()
        if (name.isBlank()) return "缺少参数 name"
        val content = arguments.stringOrNull("content")?.trim().orEmpty()
        if (content.isBlank()) return "缺少参数 content"
        val lorebook = mLorebookRepository.getLorebookById(worldbookId)
            ?: return "没有 id=$worldbookId 的世界书"
        val keys = arguments.stringList("keys")
        // 同名条目已经写过就不重复灌一遍，模型重试时不会把同一段设定塞两次
        val duplicated = mLorebookRepository.getEntriesByLorebookId(worldbookId)
            .firstOrNull { it.name == name }
        if (duplicated != null) {
            return "世界书 ${lorebook.name} 里已经有同名条目「$name」（id=${duplicated.id}），" +
                "本次未重复写入。要改内容请换一个条目名，或直接告诉用户手动编辑。"
        }
        writeWorldbookEntry(worldbookId, name, keys, content)
        val scope = if (keys.isEmpty()) "常驻条目" else "关键词：${keys.joinToString("、")}"
        return "已写入世界书 ${lorebook.name}：$name（$scope）"
    }

    /** 读取世界书已有条目，避免助手重复写入同一设定。 */
    private suspend fun getWorldbookEntries(arguments: JsonObject): String {
        val worldbookId = arguments.longOrNull("worldbookId") ?: return "缺少参数 worldbookId"
        val lorebook = mLorebookRepository.getLorebookById(worldbookId)
            ?: return "没有 id=$worldbookId 的世界书"
        val entries = mLorebookRepository.getEntriesByLorebookId(worldbookId)
        if (entries.isEmpty()) return "世界书 ${lorebook.name} 还没有条目。"
        val lines = entries.map { entry ->
            val keywords = mLorebookRepository.getEntryKeywords(entry.id)
            "id=${entry.id}\t${entry.name}\t${keywords.joinToString("、").ifBlank { "常驻" }}"
        }
        return "世界书 ${lorebook.name} 共 ${entries.size} 条：\n" + lines.joinToString("\n")
    }

    /** 角色列表里的一句话摘要，避免把整张卡灌进上下文。 */    private fun describe(character: Character): String {
        val source = character.description.ifBlank { character.personality }
        val flat = source.replace(Regex("\\s+"), " ").trim()
        return if (flat.length <= 60) flat else flat.take(60) + "…"
    }

    /** 把相对路径解析为沙箱内的绝对文件，越界返回 null。 */
    private fun resolveSandboxPath(raw: String): File? {
        val root = mSandboxRoot.canonicalFile
        root.mkdirs()
        val candidate = File(root, raw).canonicalFile
        val inside = candidate.path == root.path || candidate.path.startsWith(root.path + File.separator)
        return if (inside) candidate else null
    }

    /** 相对沙箱根的展示路径。 */
    private fun relativePath(file: File): String {
        val root = mSandboxRoot.canonicalFile.path
        val path = file.canonicalFile.path
        if (path == root) return "."
        return path.removePrefix(root + File.separator)
    }

    private companion object {
        /** 单次回填给模型的剧本 JSON 上限，避免撑爆上下文。 */
        const val MAX_SCRIPT_CHARS = 12000

        const val OUT_OF_SANDBOX = "拒绝访问：路径超出沙箱范围。"

        /** 助手写入条目的默认排序权重，与界面新建条目的初值一致。 */
        const val DEFAULT_ENTRY_ORDER = 100

        /** 助手写入条目的默认插入深度。 */
        const val DEFAULT_ENTRY_DEPTH = 4
    }


    /** 列出图库图片，供模型挑选。 */
    private suspend fun listGallery(): String {
        val images = mGalleryStore.list()
        if (images.isEmpty()) return "图库是空的。"
        return images.joinToString("\n") { "- ${it.id}｜${it.toBrief()}" }
    }

    /**
     * 按标签或名称挑一张图发出去。
     *
     * 命中多张时取第一张（列表按加入时间倒序，新图优先），
     * 并把图片标记写回结果，界面据此渲染。
     */
    private suspend fun sendImage(arguments: JsonObject): String {
        val keyword = arguments.get("keyword")?.asString.orEmpty()
        val characterName = arguments.get("character")?.asString.orEmpty()
        val hits = mGalleryStore.search(keyword, characterName)
        if (hits.isEmpty()) {
            return "图库里没有匹配「${keyword.ifBlank { "任意" }}」的图片。"
        }
        val picked = hits.first()
        return "已发送图片：${picked.name}" + nlLiteral() +
            "<image uuid=\"${picked.fileUuid}\" name=\"${picked.name}\" />"
    }

    /** 给图片追加标签。 */
    private suspend fun addImageLabel(arguments: JsonObject): String {
        val id = arguments.get("id")?.asString.orEmpty()
        val image = mGalleryStore.find(id) ?: return "找不到图片：$id"
        val extra = arguments.getAsJsonArray("labels")
            ?.mapNotNull { it.asString }
            .orEmpty()
        if (extra.isEmpty()) return "没有需要追加的标签。"
        val merged = (image.labels + extra).distinct()
        mGalleryStore.save(image.copy(labels = merged))
        return "已更新标签：${merged.joinToString("、")}"
    }

    /** 换行符，避免在拼接里反复写转义。 */
    private fun nlLiteral(): String = "\n"

}

/**
 * 读取三态布尔参数。
 *
 * 接受 JSON 布尔、`null`、以及 "true"/"false"/"yes"/"no"/"有"/"无"/"未设定" 这类字符串。
 * 缺失或无法识别时返回 `null`，由调用方决定是「保持原值」还是「写回未设定」。
 */
private fun JsonObject.triStateOrNull(key: String): Boolean? {
    val element = get(key) ?: return null
    if (element.isJsonNull) return null
    if (element.isJsonPrimitive && element.asJsonPrimitive.isBoolean) return element.asBoolean
    val raw = runCatching { element.asString.trim().lowercase() }.getOrNull() ?: return null
    return when (raw) {
        "true", "yes", "y", "1", "有", "是" -> true
        "false", "no", "n", "0", "无", "否", "没有" -> false
        // 「未设定」与「识别不出」都当没提过，保留角色卡原值，避免把已有结论覆盖成空白
        else -> null
    }
}
