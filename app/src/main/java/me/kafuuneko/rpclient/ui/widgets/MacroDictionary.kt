package me.kafuuneko.rpclient.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R

/**
 * 一条占位符说明。
 *
 * [usage] 只写「它会被替换成什么」，不写实现细节——词典是给写角色卡的人看的，
 * 不是给改解析器的人看的。
 */
data class MacroDoc(
    val macro: String,
    val usage: String
)

/**
 * 提示词占位符词典。
 *
 * 内容是 `libs/prompt/PromptMacroResolver.kt` 实际支持的宏，逐条对照过。
 * 故意不收 `{{prompt}}`、`{{match}}`、`{{lorebook_schema}}` 这类只在图像生成、
 * 正则脚本、角色向导内部使用的宏——它们写进角色卡不会生效，列出来只会让人白试。
 */
object MacroDictionary {

    /** 人称：最常用，两个。 */
    val PERSON = listOf(
        MacroDoc("{{char}}", "角色名。群聊里是当前正在说话的那个角色。"),
        MacroDoc("{{user}}", "你的名字。取自设置里的显示名，会话可以单独覆盖。")
    )

    /** 角色卡自身的字段。 */
    val CHARACTER = listOf(
        MacroDoc("{{description}}", "角色描述。"),
        MacroDoc("{{personality}}", "性格设定。"),
        MacroDoc("{{scenario}}", "场景设定。"),
        MacroDoc("{{charCreatorNotes}}", "角色备注。默认不进提示词，只有这里引用才会出现。"),
        MacroDoc("{{creator}}", "角色卡作者。"),
        MacroDoc("{{charVersion}}", "角色卡版本号。"),
        MacroDoc("{{charNote}}", "Character's Note 的正文，按深度插进聊天历史里。"),
        MacroDoc("{{mesExamples}}", "对话示例。"),
        MacroDoc("{{charFirstMessage}}", "第一条开场白。"),
        MacroDoc("{{charFirstMessage::N}}", "第 N 条开场白，N 从 0 开始，含备用开场白。"),
        MacroDoc("{{charInstruction}}", "后置提示词（Post-History Instructions）。")
    )

    /** 当前这轮对话的运行时状态。 */
    val SESSION = listOf(
        MacroDoc("{{persona}}", "你的人设描述。"),
        MacroDoc("{{history}}", "格式化之后的聊天历史。"),
        MacroDoc("{{summary}}", "滚动摘要。没有开摘要时是空串。"),
        MacroDoc("{{lastMessage}}", "最近一条消息的正文。"),
        MacroDoc("{{lastMessageId}}", "最近一条消息的 ID。"),
        MacroDoc("{{lastUserMessage}}", "最近一条你说的话。"),
        MacroDoc("{{lastCharMessage}}", "最近一条角色说的话。"),
        MacroDoc("{{firstIncludedMessageId}}", "本轮上下文里最早那条消息的 ID。"),
        MacroDoc("{{original}}", "原始模板本身。角色级主提示词用它引用全局那份。"),
        MacroDoc("{{group}}", "群聊成员名单。单聊里是空串。"),
        MacroDoc("{{charIfNotGroup}}", "群聊里只有一个角色时是那个角色名，多个角色时为空。")
    )

    /** 世界书插槽。 */
    val LOREBOOK = listOf(
        MacroDoc(
            "{{outlet::名字}}",
            "把插入位置设为 outlet、插槽名相同的世界书条目注入到这里。"
        )
    )

    /** 时间与模型信息。 */
    val ENVIRONMENT = listOf(
        MacroDoc("{{time}}", "当前时间，形如 21:07。"),
        MacroDoc("{{date}}", "今天的日期，形如 2026-11-03。"),
        MacroDoc("{{weekday}}", "星期几，跟随系统语言。"),
        MacroDoc("{{isotime}}", "完整的 ISO 日期时间。"),
        MacroDoc("{{isodate}}", "ISO 日期。"),
        MacroDoc("{{model}}", "当前模型 ID。"),
        MacroDoc("{{maxContextTokens}}", "上下文上限。"),
        MacroDoc("{{maxResponseTokens}}", "回复预留上限。"),
        MacroDoc("{{maxPrompt}}", "留给提示词的预算，等于上面两个之差。")
    )

    /** 排版用。 */
    val FORMAT = listOf(
        MacroDoc("{{newline}}", "一个换行。"),
        MacroDoc("{{newline::N}}", "N 个换行。"),
        MacroDoc("{{space::N}}", "N 个空格。"),
        MacroDoc("{{noop}}", "替换成空串。用来把某段文字临时注释掉。"),
        MacroDoc("<START>", "对话示例里分隔不同轮次的标记。")
    )

    /** 故事与冒险模式专用的宏。 */
    val STORY = listOf(
        MacroDoc("{{guidance}}", "故事/冒险里的引导段。"),
        MacroDoc("{{memory}}", "故事里的长期记忆。"),
        MacroDoc("{{words}}", "摘要提示词里的字数提示常量。")
    )

    /** 对话框里的分组顺序。 */
    val SECTIONS: List<Pair<String, List<MacroDoc>>> = listOf(
        "人称" to PERSON,
        "角色卡" to CHARACTER,
        "当前对话" to SESSION,
        "世界书" to LOREBOOK,
        "时间与模型" to ENVIRONMENT,
        "排版" to FORMAT,
        "故事与冒险" to STORY
    )
}

/**
 * 占位符词典对话框。
 *
 * [onInsert] 不为空时每条右侧给一个「插入」按钮，可以把宏直接填进正在编辑的输入框；
 * 从设置页打开时为 null，只当查阅用。
 */
@Composable
fun MacroDictionaryDialog(
    onDismiss: () -> Unit,
    onInsert: ((String) -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.macro_dictionary_title)) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MacroDictionary.SECTIONS.forEach { (title, macros) ->
                    item(key = "section-" + title) {
                        Text(
                            text = title,
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(macros, key = { it.macro }) { doc ->
                        MacroRow(doc = doc, onInsert = onInsert)
                    }
                }
                item(key = "footer") {
                    Text(
                        text = stringResource(R.string.macro_dictionary_footer),
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

/** 单条：宏名等宽显示，说明换行缩进。 */
@Composable
private fun MacroRow(doc: MacroDoc, onInsert: ((String) -> Unit)?) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            ) {
                Text(
                    text = doc.macro,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (onInsert != null) {
                TextButton(onClick = { onInsert(doc.macro) }) {
                    Text(
                        text = stringResource(R.string.macro_dictionary_insert),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        Text(
            text = doc.usage,
            modifier = Modifier.padding(top = 2.dp, start = 2.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
