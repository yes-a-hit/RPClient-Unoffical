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

    /**
     * 「全员」这个默认场景的固定 id。
     *
     * 「全员」原本只是「没设场景」的另一种说法，界面上却是一个跟别的场景
     * 长得不一样的标签——用户看不出它也能配背景、也能改名字。
     * 给它一个固定 id 之后它就是普通场景了：照样能编辑、能有自己的背景，
     * 只是不参与成员过滤（[allowedCharacterIds] 对它永远返回 null）。
     */
    const val DEFAULT_SCENE_ID = "*"

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
        // 「全员」不是一个过滤条件，回到它等于回到不设场景
        if (activeSceneId == DEFAULT_SCENE_ID) return null
        return scenes.firstOrNull { it.id == activeSceneId }
    }

    /** 「全员」场景的展示配置；没配过返回 null。 */
    fun defaultScene(scenes: List<GroupChatScene>): GroupChatScene? =
        scenes.firstOrNull { it.id == DEFAULT_SCENE_ID }

    /**
     * 页面上真正在用的那一场，包含「全员」。
     *
     * [active] 对「全员」返回 null（它不过滤成员），但背景、提示词这些
     * 展示层的东西还是要认它，所以另给一个不吞掉默认场景的入口。
     */
    fun activeForDisplay(scenes: List<GroupChatScene>, activeSceneId: String?): GroupChatScene? {
        val id = activeSceneId?.takeIf { it.isNotBlank() } ?: DEFAULT_SCENE_ID
        return scenes.firstOrNull { it.id == id }
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

    /**
     * 「+」菜单里的场景行动：进哪个、或者回到全员。
     *
     * 进出场景走显式动作而不是关键字匹配——一句话里出现地名太容易误判，
     * 角色说「厨房里好像有动静」也会被当成要进厨房。
     */
    fun movements(scenes: List<GroupChatScene>, activeSceneId: String?): List<SceneMovement> {
        val active = active(scenes, activeSceneId)
        val enter = scenes
            .filter { it.id != active?.id && it.id != DEFAULT_SCENE_ID }
            .map { SceneMovement(sceneId = it.id, name = it.name, isLeave = false) }
        if (active == null) return enter
        return enter + SceneMovement(sceneId = "", name = active.name, isLeave = true)
    }

    /** 一条可执行的动作：进某个场景，或者从当前场景退回全员。 */
    data class SceneMovement(val sceneId: String, val name: String, val isLeave: Boolean)

    /**
     * 按关键字猜「玩家这句话是往哪走」。
     *
     * 只在实验开关打开时被调用。判据是「命中字数最多的那个场景」——
     * 同一个词被两个场景共享时，长的那个更具体，也更容易是用户的真实意图。
     * 已经在当前场景时不算「往这里走」，返回 null，避免原地刷一轮旁白。
     *
     * 返回 null 还有一层含义：不确定。宁可不动，也不要为了「有反应」
     * 把整场搬到用户没打算去的地方。
     */
    fun matchByKeyword(
        scenes: List<GroupChatScene>,
        text: String,
        activeSceneId: String?,
    ): GroupChatScene? {
        val line = text.trim()
        if (line.isEmpty()) return null
        if (line.startsWith("/")) return null
        val current = active(scenes, activeSceneId)
        return scenes
            .asSequence()
            .filter { it.id != DEFAULT_SCENE_ID && it.id != current?.id }
            .mapNotNull { scene ->
                val hit = scene.keywords
                    .asSequence()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && line.contains(it, ignoreCase = true) }
                    .maxOfOrNull { it.length }
                if (hit == null) null else scene to hit
            }
            .maxByOrNull { it.second }
            ?.first
    }
}
