package me.kafuuneko.rpclient.libs.script

/**
 * 回复越界判定。
 *
 * 与 [ScriptPlayEngine] 分开是因为两者的对象不同：引擎判的是
 * 「玩家做了什么，该有什么后果」，这里判的是「模型写了不该写的东西」。
 * 前者决定剧情走向，后者在剧情开始之前就把内容挡掉。
 *
 * 全部为纯函数，方便单测；调用方负责把结果落成界面状态。
 */
object ScriptPlayGuard {
    /** 命中结果。 */
    sealed interface Hit {
        /** 命中的那条规则。 */
        val rule: ScriptGuardRule

        /**
         * 只作废这一轮。
         *
         * 回复不进消息列表，模型下一轮拿不到它，
         * 玩家的输入也退回去让玩家重来。
         */
        data class Soft(override val rule: ScriptGuardRule) : Hit

        /** 直接判本场失败，走 [ScriptGuardRule.failSceneId]。 */
        data class Hard(override val rule: ScriptGuardRule) : Hit
    }

    /**
     * 检查一段回复。
     *
     * 按给定顺序取第一条命中的：作者把最要紧的写在前面，
     * 同时命中「崩了玩偶」和「掏枪」时，应该报的是前者。
     *
     * @param rules 剧本级规则在前、场景级在后，由调用方拼好
     * @param reply 本轮模型回复的正文
     */
    fun evaluate(rules: List<ScriptGuardRule>, reply: String): Hit? {
        if (rules.isEmpty() || reply.isBlank()) return null
        val rule = rules.firstOrNull { matches(it, reply) } ?: return null
        return if (rule.hardFail) Hit.Hard(rule) else Hit.Soft(rule)
    }

    /**
     * 单条规则是否命中。
     *
     * 正则写坏了按不命中处理：一条规则写错不该让整局玩不下去，
     * 也不该反过来把所有回复都判成越界。
     */
    fun matches(rule: ScriptGuardRule, reply: String): Boolean {
        val pattern = rule.pattern.trim()
        if (pattern.isEmpty()) return false
        if (rule.regex) {
            return runCatching { Regex(pattern).containsMatchIn(reply) }.getOrDefault(false)
        }
        // 与失败条件同一套写法：竖线分隔多个说法，命中任意一个即可
        return pattern.split('|').any { word ->
            val trimmed = word.trim()
            trimmed.isNotEmpty() && reply.contains(trimmed)
        }
    }

    /** 命中后展示给玩家的一句话。 */
    fun describe(hit: Hit): String =
        hit.rule.description.ifBlank {
            if (hit.rule.hardFail) "剧情越界，本场结束" else "这一轮的内容超出了本场设定"
        }

    /**
     * 作废一轮时插进对话的系统提示。
     *
     * 带上规则说明与重写提示：只说「重来一次」，模型下一轮会原样再写一遍；
     * 说清楚绕开什么，它才知道该换方向。
     */
    fun retryNotice(hit: Hit): String = buildString {
        append("（这一轮作废：").append(describe(hit)).append("）")
        val hint = hit.rule.retryHint.trim()
        if (hint.isNotEmpty()) append(' ').append(hint)
    }

    /**
     * 给系统提示词的禁区段。
     *
     * 拦截是兜底，能不让模型写出来最好——它写出来再作废，
     * 玩家会看到一句话之后又消失，体验很差。
     * 没有规则时返回 null，避免提示词里多一段空标题。
     */
    fun promptSection(rules: List<ScriptGuardRule>): String? {
        val described = rules.filter { it.pattern.isNotBlank() }.mapNotNull { rule ->
            val text = rule.description.trim().ifBlank { rule.pattern.trim() }
            val hint = rule.retryHint.trim()
            if (hint.isEmpty()) "- $text" else "- $text（改为：$hint）"
        }
        if (described.isEmpty()) return null
        return buildString {
            append("\n【禁止描写】以下内容无论玩家怎么引导都不许写出来：\n")
            described.forEach { append(it).append('\n') }
            append(
                "如果玩家的行动会引出这些结果，就让行动失败、被打断、或者只写到" +
                    "擦边的边缘为止，不要真的把结果写出来。\n"
            )
        }
    }
}
