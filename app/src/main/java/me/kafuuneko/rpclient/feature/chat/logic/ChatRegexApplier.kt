package me.kafuuneko.rpclient.feature.chat.logic

import me.kafuuneko.rpclient.libs.regex.RegexMessageProcessor
import me.kafuuneko.rpclient.libs.regex.RegexScriptRepository
import me.kafuuneko.rpclient.libs.regex.RegexScriptRuntime
import me.kafuuneko.rpclient.libs.room.repository.CharacterRepository
import me.kafuuneko.rpclient.libs.room.repository.ChatRepository

/**
 * 把「会话 ID」翻译成「该会话此刻生效的正则脚本与宏」这件事收在一处。
 *
 * 单聊页原先自己查会话、查角色、查脚本、拼宏，再调处理器，
 * 四个仓库的取数顺序散落在四个方法里。抽出来之后 ViewModel 只表达
 * 「这段用户输入要过用户输入正则」，不再关心脚本从哪来。
 *
 * 流式生成的收尾不走这里：那条路径必须使用生成启动时冻结的快照
 * （收尾可能运行在协程取消后的 NonCancellable 区域，此时再查库会
 * 与用户途中改脚本的行为打架）。
 */
class ChatRegexApplier(
    private val mChatRepository: ChatRepository,
    private val mCharacterRepository: CharacterRepository,
    private val mRegexRepository: RegexScriptRepository,
    private val mRegexProcessor: RegexMessageProcessor,
) {

    /**
     * 用户输入写入会话前执行 UserInput 正则。
     *
     * @param sessionId 会话 ID
     * @param input 原始用户文本
     * @param isEdit 是否为编辑已有消息，为真时启用 runOnEdit 约束
     */
    suspend fun forUserInput(sessionId: Long, input: String, isEdit: Boolean = false): String {
        val context = resolve(sessionId) ?: return input
        return mRegexProcessor.applyUserInput(input, context.scripts, context.macros, isEdit)
    }

    /**
     * 模型回复落库前执行 Source 正则。
     *
     * @param sessionId 会话 ID
     * @param input 原始生成文本
     * @param isEdit 是否为编辑已有消息
     */
    suspend fun forAiResponse(sessionId: Long, input: String, isEdit: Boolean = false): String {
        val context = resolve(sessionId) ?: return input
        return mRegexProcessor.applyAiResponse(
            input = input,
            scripts = context.scripts,
            macros = context.macros,
            isEdit = isEdit,
        )
    }

    /** 会话与角色任一缺失时返回 null，调用方按原文放行。 */
    private suspend fun resolve(sessionId: Long): Context? {
        val session = mChatRepository.getSessionById(sessionId) ?: return null
        val character = mCharacterRepository.getCharacterById(session.characterId) ?: return null
        return Context(
            scripts = mRegexRepository.activeScripts(listOf(character)),
            macros = RegexScriptRuntime.macros(
                session.userName,
                character.name,
                session.userDescription,
                character.scenario,
            ),
        )
    }

    private data class Context(
        val scripts: List<me.kafuuneko.rpclient.libs.regex.ScopedRegexScript>,
        val macros: Map<String, String>,
    )
}
