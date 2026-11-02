package me.kafuuneko.rpclient.libs.groupchat

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatScene
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatSceneCommand

/**
 * 群聊场景的存取与解析。
 *
 * 场景列表存在会话实体的 JSON 字段里，不单独建表：
 * 场景数量是个位数，跟着会话走才不会出现「会话没了场景还留着」。
 */
object GroupChatScenes {

    /** 指令前缀，顺序决定匹配优先级（长的在前，避免 `/去` 吃掉 `/清除`）。 */
    private val SWITCH_PREFIXES = listOf("/去 ", "/前往 ", "/scene ", "/go ")
    private val CLEAR_PREFIXES = listOf("/清除场景", "/离开场景", "/scene off", "/scene clear")

    private val mListType = object : TypeToken<List<GroupChatScene>>() {}.type

    /**
     * 反序列化场景列表。
     *
     * 任何异常都返回空列表：场景配置坏掉不该让整个群聊打不开，
     * 顶多是这次没有场景可用。
     */
    fun decode(json: String?): List<GroupChatScene> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            Gson().fromJson<List<GroupChatScene>>(json, mListType)
                ?.filter { it.name.isNotBlank() }
                ?.map { scene ->
                    // 老数据可能缺 id，补一个稳定的：名字哈希足够，
                    // 名字重复的场景本来就是配置错误
                    if (scene.id.isBlank()) scene.copy(id = "scene-" + scene.name.hashCode()) else scene
                }
                .orEmpty()
        }.getOrDefault(emptyList())
    }

    /** 序列化场景列表。 */
    fun encode(scenes: List<GroupChatScene>): String =
        runCatching { Gson().toJson(scenes) }.getOrDefault("[]")

    /** 按名称找场景，忽略首尾空白；找不到返回 null。 */
    fun findByName(scenes: List<GroupChatScene>, name: String): GroupChatScene? {
        val target = name.trim()
        if (target.isEmpty()) return null
        return scenes.firstOrNull { it.name.trim() == target }
            ?: scenes.firstOrNull { it.name.contains(target, ignoreCase = true) }
    }

    /** 按 id 找当前生效的场景；未设置或找不到时返回 null。 */
    fun active(scenes: List<GroupChatScene>, activeSceneId: String?): GroupChatScene? {
        if (activeSceneId.isNullOrBlank()) return null
        return scenes.firstOrNull { it.id == activeSceneId }
    }

    /**
     * 算出本场实际在场的角色 ID。
     *
     * 返回 null 表示「不做限制」，调用方按原样用全部成员；
     * 返回集合表示只留这些角色。用 null 而不是全集，是为了让调用方
     * 能区分「场景没限制」和「场景恰好包含了所有人」，前者不该改写任何提示词。
     */
    fun allowedCharacterIds(scene: GroupChatScene?): Set<Long>? {
        if (scene == null) return null
        if (!scene.restrictsMembers) return null
        return scene.characterIds.toSet()
    }

    /**
     * 本场只启用哪些世界书条目。
     *
     * 返回 null 表示「不做限制」，沿用会话原本勾选的那批；
     * 返回集合表示只认这些。和 [allowedCharacterIds] 同一个约定，
     * 让调用方能区分「场景没限制」和「场景恰好包含了全部条目」。
     */
    fun allowedLorebookEntryIds(scene: GroupChatScene?): Set<Long>? {
        if (scene == null) return null
        if (!scene.restrictsLorebook) return null
        return scene.lorebookEntryIds.toSet()
    }

    /**
     * 当前场景下真正生效的世界书条目。
     *
     * 场景勾了就是覆盖，不是取交集：用户给「厨房」勾了两条炖汤的设定，
     * 进厨房时就该只有这两条在起作用，原先勾的十条先让位。
     * 场景一条都没勾，说明这一场不需要特别限制，原样沿用会话的设置。
     */
    fun effectiveLorebookEntryIds(
        scene: GroupChatScene?,
        sessionEntryIds: Set<Long>
    ): Set<Long> {
        val allowed = allowedLorebookEntryIds(scene) ?: return sessionEntryIds
        return allowed
    }

    /**
     * 解析一条用户输入是否为场景指令。
     *
     * 只认行首前缀：正文里出现「/去 厨房」不该被劫持，
     * 否则角色说到斜杠就变成命令了。
     */
    fun parseCommand(text: String): GroupChatSceneCommand {
        val line = text.trim()
        if (!line.startsWith("/")) return GroupChatSceneCommand.NotACommand

        CLEAR_PREFIXES.firstOrNull { line.startsWith(it, ignoreCase = true) }?.let {
            return GroupChatSceneCommand.Clear
        }
        SWITCH_PREFIXES.firstOrNull { line.startsWith(it, ignoreCase = true) }?.let { prefix ->
            val name = line.removePrefix(prefix).trim()
            if (name.isEmpty()) return GroupChatSceneCommand.NotACommand
            return GroupChatSceneCommand.SwitchTo(name)
        }
        return GroupChatSceneCommand.NotACommand
    }

    /** 生成一个新场景，id 用时间戳加名字哈希，避免同一毫秒撞车。 */
    fun newScene(
        name: String,
        characterIds: List<Long> = emptyList(),
        lorebookEntryIds: List<Long> = emptyList()
    ): GroupChatScene =
        GroupChatScene(
            id = "scene-" + System.currentTimeMillis() + "-" + name.hashCode(),
            name = name.trim(),
            characterIds = characterIds,
            lorebookEntryIds = lorebookEntryIds
        )
}
