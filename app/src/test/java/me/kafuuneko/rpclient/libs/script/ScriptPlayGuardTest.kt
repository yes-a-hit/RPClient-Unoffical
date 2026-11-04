package me.kafuuneko.rpclient.libs.script

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 越界拦截的判定。
 *
 * 这些用例描述的是「什么样的一段回复该被拦下来」，
 * 改动 [ScriptPlayGuard] 之前先看这里，别把已经定好的边界改宽了。
 */
class ScriptPlayGuardTest {
    @Test
    fun `no rules means nothing is intercepted`() {
        assertNull(ScriptPlayGuard.evaluate(emptyList(), "保安抽枪崩了玩偶"))
    }

    @Test
    fun `blank reply is never intercepted`() {
        val rule = ScriptGuardRule(pattern = "抽枪")
        assertNull(ScriptPlayGuard.evaluate(listOf(rule), "   "))
    }

    @Test
    fun `keyword rule intercepts a soft hit`() {
        val rule = ScriptGuardRule(pattern = "抽枪", description = "本场没有枪")
        val hit = ScriptPlayGuard.evaluate(listOf(rule), "保安抽枪崩了玩偶")
        assertNotNull(hit)
        assertTrue(hit is ScriptPlayGuard.Hit.Soft)
        assertEquals("本场没有枪", ScriptPlayGuard.describe(hit!!))
    }

    @Test
    fun `pipe separated alternatives all match`() {
        val rule = ScriptGuardRule(pattern = "抽枪|开枪|射击")
        assertNotNull(ScriptPlayGuard.evaluate(listOf(rule), "他直接开枪了"))
        assertNotNull(ScriptPlayGuard.evaluate(listOf(rule), "远处传来射击声"))
    }

    @Test
    fun `regex rule matches by pattern`() {
        val rule = ScriptGuardRule(pattern = "玩偶.{0,4}(死了|倒下)", regex = true)
        assertNotNull(ScriptPlayGuard.evaluate(listOf(rule), "那个玩偶已经倒下"))
        assertNull(ScriptPlayGuard.evaluate(listOf(rule), "玩偶在台上笑了笑"))
    }

    @Test
    fun `broken regex is treated as no match`() {
        val rule = ScriptGuardRule(pattern = "([unclosed", regex = true)
        assertNull(ScriptPlayGuard.evaluate(listOf(rule), "随便什么内容"))
    }

    @Test
    fun `hard fail wins over soft`() {
        val rule = ScriptGuardRule(pattern = "抽枪", hardFail = true, failSceneId = "lose")
        val hit = ScriptPlayGuard.evaluate(listOf(rule), "保安抽枪")
        assertTrue(hit is ScriptPlayGuard.Hit.Hard)
        assertEquals("lose", (hit as ScriptPlayGuard.Hit.Hard).rule.failSceneId)
    }

    @Test
    fun `first matching rule wins`() {
        val first = ScriptGuardRule(pattern = "玩偶", description = "先写的先算")
        val second = ScriptGuardRule(pattern = "玩偶", description = "后写的")
        val hit = ScriptPlayGuard.evaluate(listOf(first, second), "玩偶动了")
        assertEquals("先写的先算", ScriptPlayGuard.describe(hit!!))
    }

    @Test
    fun `blank pattern rule never matches`() {
        val rule = ScriptGuardRule(pattern = "   ", description = "空规则")
        assertNull(ScriptPlayGuard.evaluate(listOf(rule), "任何内容"))
    }

    @Test
    fun `retry notice carries the hint`() {
        val rule = ScriptGuardRule(
            pattern = "抽枪",
            description = "本场没有枪",
            retryHint = "保安只有一根警棍"
        )
        val hit = ScriptPlayGuard.evaluate(listOf(rule), "保安抽枪")!!
        val notice = ScriptPlayGuard.retryNotice(hit)
        assertTrue(notice.contains("本场没有枪"))
        assertTrue(notice.contains("保安只有一根警棍"))
    }

    @Test
    fun `retry notice falls back to a generic line`() {
        val rule = ScriptGuardRule(pattern = "抽枪")
        val hit = ScriptPlayGuard.evaluate(listOf(rule), "保安抽枪")!!
        assertTrue(ScriptPlayGuard.retryNotice(hit).contains("超出了本场设定"))
    }

    @Test
    fun `prompt section is absent when there is nothing to forbid`() {
        assertNull(ScriptPlayGuard.promptSection(emptyList()))
        assertNull(ScriptPlayGuard.promptSection(listOf(ScriptGuardRule(pattern = " "))))
    }

    @Test
    fun `prompt section lists every rule with its hint`() {
        val section = ScriptPlayGuard.promptSection(
            listOf(
                ScriptGuardRule(pattern = "抽枪", description = "本场没有枪"),
                ScriptGuardRule(pattern = "诈尸", retryHint = "玩偶只能是机械故障")
            )
        )!!
        assertTrue(section.contains("本场没有枪"))
        assertTrue(section.contains("机械故障"))
        // 没写描述的规则退回用匹配片段，不能变成一行空破折号
        assertTrue(section.contains("诈尸"))
    }
}
