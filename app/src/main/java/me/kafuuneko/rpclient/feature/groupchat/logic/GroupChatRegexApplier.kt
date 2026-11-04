package me.kafuuneko.rpclient.feature.groupchat.logic

import me.kafuuneko.rpclient.libs.regex.RegexMessageProcessor
import me.kafuuneko.rpclient.libs.regex.RegexScriptRepository
import me.kafuuneko.rpclient.libs.regex.RegexScriptRuntime
import me.kafuuneko.rpclient.libs.regex.ScopedRegexScript
import me.kafuuneko.rpclient.libs.room.entity.Character
import me.kafuuneko.rpclient.libs.room.entity.GroupChatSession

/**
 * 群聊的正则装配：查生效脚本、拼宏、按阶段套用。
 *
 * 从 ViewModel 抽出来的原因不是行数，是**同一份上下文被算了三遍**：
 * 用户输入要一遍、角色回复要一遍、编辑消息又要一遍，每次都重新查库、
 * 重新拼宏。调用方现在把「这场群聊是谁在说话」描述一次，剩下的交给这里。
 *
 * 流式生成的收尾不走这里：那条路径要用开播时冻结的脚本与宏快照，
 * 否则用户中途改脚本会让同一条回复前后两半套不同规则。
 */
class GroupChatRegexApplier(
    private val mRegexRepository: RegexScriptRepository,
    private val mRegexProcessor: RegexMessageProcessor,
) {

    /**
     * 一次装配的结果：脚本与宏都就绪，后续多个角色复用。
     *
     * @param scripts 参与本次群聊的角色所激活的脚本
     * @param context 拼宏所需的会话与人名信息
     */
    data class Prepared(
        val scripts: List<ScopedRegexScript>,
        val context: Context,
    ) {
        /** 取某个发言者的宏字典；传空串表示「不针对具体角色」。 */
        fun macrosFor(speakerName: String): Map<String, String> = GroupChatRegexApplier.macros(context, speakerName)
    }

    /** 群聊正则宏的原料。人数与顺序会影响 `{{group}}`，所以要显式带上。 */
    data class Context(
        val userName: String,
        val userDescription: String,
        val scenario: String,
        val memberNames: List<String>,
    )

    companion object {
        /** 拼出一套宏字典。`{{char}}` 取发言者，`{{group}}` 取全体成员名单。 */
        fun macros(context: Context, speakerName: String): Map<String, String> = RegexScriptRuntime.macros(
            userName = context.userName,
            characterName = speakerName,
            userDescription = context.userDescription,
            scenario = context.scenario,
            groupNames = context.memberNames.joinToString(", "),
        )
    }

    /** 装配一次，供本轮多次套用。 */
    suspend fun prepare(characters: List<Character>, session: GroupChatSession, memberNames: List<String>): Prepared =
        Prepared(
            scripts = mRegexRepository.activeScripts(characters),
            context = Context(
                userName = session.userName,
                userDescription = session.userDescription,
                scenario = session.scenario,
                memberNames = memberNames,
            ),
        )

    /** 用户输入落库前执行 UserInput 阶段。 */
    fun applyUserInput(prepared: Prepared, input: String, isEdit: Boolean = false): String =
        mRegexProcessor.applyUserInput(
            input = input,
            scripts = prepared.scripts,
            macros = prepared.macrosFor(""),
            isEdit = isEdit,
        )

    /** 角色回复落库前执行 Source 阶段。 */
    fun applyAiResponse(prepared: Prepared, speakerName: String, input: String, isEdit: Boolean = false): String =
        mRegexProcessor.applyAiResponse(
            input = input,
            scripts = prepared.scripts,
            macros = prepared.macrosFor(speakerName),
            isEdit = isEdit,
        )
}
