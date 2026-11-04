package me.kafuuneko.rpclient.libs.groupchat.model

/**
 * 群聊里的一个场景。
 *
 * 与剧本场景的区别：剧本场景是作者写死的过场，这里是纯运行时分组——
 * 同一批群成员，按「现在待在哪儿」拆成几拨人，
 * 换场地就换一批人说话，不用另建群。
 *
 * @property characterIds 驻扎在本场的角色 ID；空列表表示全员都在，
 * 这样没配置过场景的旧群聊行为完全不变。
 */
data class GroupChatScene(
    val id: String = "",
    val name: String = "",
    /** 场景设定，进入 Prompt 时叠加在群聊场景之上。 */
    val description: String = "",
    /** 驻扎在本场的角色 ID；空列表等于所有人。 */
    val characterIds: List<Long> = emptyList(),
    /** 进入场景时追加的一条旁白提示，可为空。 */
    val entryPrompt: String = "",
    /**
     * 本场只用这些世界书条目；空列表等于不干预，沿用会话已启用的那批。
     *
     * 和 [characterIds] 一样是「收窄」而不是「替换」：配了才生效，
     * 没配过的旧场景一个字节都不用改，行为跟以前一模一样。
     */
    val lorebookEntryIds: List<Long> = emptyList(),
    /**
     * 这次移动别人看不看得见。
     *
     * 公开走＝谁走了大家都清楚，留下的人会追问；
     * 悄悄走＝只有跟着走的人知道，留下的那些人此刻还蒙在鼓里。
     */
    val visibility: SceneVisibility = SceneVisibility.Open,
    /** 本场专用背景图（文件 uuid）；空表示沿用会话背景。 */
    val backgroundUuid: String = "",
    /**
     * 命中这些词就认为玩家在往这里走。
     *
     * 只在「实验性模块 → 关键字换场」打开时生效。默认关闭，因为误判代价很高：
     * 角色说一句「厨房里好像有动静」不该把整场搬去厨房。
     */
    val keywords: List<String> = emptyList(),
) {
    /** 本场是否限定了具体成员。 */
    val restrictsMembers: Boolean
        get() = characterIds.isNotEmpty()

    /** 本场是否限定了世界书条目。 */
    val restrictsLorebook: Boolean
        get() = lorebookEntryIds.isNotEmpty()
}

/**
 * 场景切换的可见性。
 *
 * 只有两态：别人看得见，和别人看不见。中间状态（「有人看见了有人没看见」）
 * 交给模型按人物关系自己编，不做成开关——那样配置成本会超过收益。
 */
enum class SceneVisibility {
    /** 公开：离开与同行都是明面上的事。 */
    Open,

    /** 隐蔽：绕开众人视线离开，在场的人事后才察觉。 */
    Secret;

    companion object {
        fun fromName(raw: String?): SceneVisibility =
            entries.firstOrNull { it.name.equals(raw?.trim(), ignoreCase = true) } ?: Open
    }
}

/**
 * 场景切换的识别结果。
 *
 * 单独一个 sealed 而不是返回可空字符串：调用方要能区分
 * 「不是场景指令」和「是指令但场景不存在」，后者得回一句提示。
 */
sealed class GroupChatSceneCommand {
    /** 不是场景指令，按普通消息处理。 */
    data object NotACommand : GroupChatSceneCommand()

    /** 切到指定场景。 */
    data class SwitchTo(val sceneName: String) : GroupChatSceneCommand()

    /** 回到「不限场景」，也就是全员都在。 */
    data object Clear : GroupChatSceneCommand()
}
