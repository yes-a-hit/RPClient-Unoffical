package me.kafuuneko.rpclient.libs.groupchat

import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatScene
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** 关键字换场：只在实验开关打开时才会被调用，这里只验纯函数。 */
class GroupChatSceneKeywordTest {

    private fun scene(id: String, name: String, keywords: List<String>) =
        GroupChatScene(id = id, name = name, keywords = keywords)

    private val kitchen = scene("k", "厨房", listOf("厨房", "灶台"))
    private val bedroom = scene("b", "卧室", listOf("卧室", "床上"))
    private val scenes = listOf(kitchen, bedroom)

    @Test
    fun `matches the scene whose keyword appears`() {
        assertEquals(kitchen, GroupChatScenes.matchByKeyword(scenes, "我去厨房看看", null))
    }

    @Test
    fun `returns null when nothing matches`() {
        assertNull(GroupChatScenes.matchByKeyword(scenes, "今天天气不错", null))
    }

    @Test
    fun `already being there is not a match`() {
        // 已经在厨房还说「厨房」，不该再刷一轮旁白
        assertNull(GroupChatScenes.matchByKeyword(scenes, "厨房好乱", "k"))
    }

    @Test
    fun `longer keyword wins when two scenes both hit`() {
        val vague = scene("v", "一楼", listOf("楼"))
        val precise = scene("p", "二楼卧室", listOf("二楼卧室"))
        val list = listOf(vague, precise)
        assertEquals(precise, GroupChatScenes.matchByKeyword(list, "我们上二楼卧室吧", null))
    }

    @Test
    fun `slash commands are never keyword matched`() {
        assertNull(GroupChatScenes.matchByKeyword(scenes, "/去 厨房", null))
    }

    @Test
    fun `default scene never matches`() {
        val all = scene(GroupChatScenes.DEFAULT_SCENE_ID, "全员", listOf("大家"))
        assertNull(GroupChatScenes.matchByKeyword(listOf(all), "大家都过来", null))
    }

    @Test
    fun `blank input matches nothing`() {
        assertNull(GroupChatScenes.matchByKeyword(scenes, "   ", null))
    }
}
