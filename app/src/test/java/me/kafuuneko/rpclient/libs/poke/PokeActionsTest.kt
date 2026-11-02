package me.kafuuneko.rpclient.libs.poke

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** 动作预设的解析与渲染。 */
class PokeActionsTest {

    @Test
    fun `blank config yields the three built-ins`() {
        val actions = PokeActions.parse("")
        assertEquals(3, actions.size)
        assertEquals(
            listOf(PokeActions.KEY_POKE, PokeActions.KEY_HEADPAT, PokeActions.KEY_CHEEK),
            actions.map { it.id }
        )
    }

    @Test
    fun `round trips through json`() {
        val custom = listOf(
            PokeAction("a", "挠痒", "（挠了挠 {name} 的痒）"),
            PokeAction("b", "握手", "（和 {name} 握了握手）")
        )
        val restored = PokeActions.parse(PokeActions.serialize(custom))
        assertEquals(custom, restored)
    }

    @Test
    fun `broken json falls back to defaults`() {
        // 手改坏了不该让菜单整片空掉
        assertEquals(PokeActions.DEFAULTS, PokeActions.parse("{not json"))
    }

    @Test
    fun `empty array also falls back`() {
        assertEquals(PokeActions.DEFAULTS, PokeActions.parse("[]"))
    }

    @Test
    fun `entries without template are skipped`() {
        val raw = """[{"id":"x","label":"只有标签"},{"id":"y","label":"好的","template":"（拍了拍 {name}）"}]"""
        val actions = PokeActions.parse(raw)
        assertEquals(1, actions.size)
        assertEquals("y", actions.single().id)
    }

    @Test
    fun `renders name into template`() {
        val action = PokeAction("p", "戳", "（戳了戳 {name}）")
        assertEquals("（戳了戳 阿夜）", PokeActions.render(action, "阿夜"))
    }

    @Test
    fun `template without placeholder stays untouched`() {
        val action = PokeAction("p", "拍手", "（拍了拍手）")
        assertTrue(PokeActions.render(action, "谁都不影响").contains("拍了拍手"))
    }
}
