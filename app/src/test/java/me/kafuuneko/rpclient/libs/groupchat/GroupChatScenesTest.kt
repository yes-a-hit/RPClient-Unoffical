package me.kafuuneko.rpclient.libs.groupchat

import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatScene
import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatSceneCommand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 群聊场景的解析与筛选口径。 */
class GroupChatScenesTest {

    @Test
    fun `中文去指令在行首时被识别`() {
        val command = GroupChatScenes.parseCommand("/去 厨房")
        assertEquals(GroupChatSceneCommand.SwitchTo("厨房"), command)
    }

    @Test
    fun `英文前缀同样识别`() {
        assertEquals(
            GroupChatSceneCommand.SwitchTo("kitchen"),
            GroupChatScenes.parseCommand("/go kitchen")
        )
    }

    @Test
    fun `清除指令回到不限场景`() {
        assertEquals(
            GroupChatSceneCommand.Clear,
            GroupChatScenes.parseCommand("/清除场景")
        )
    }

    @Test
    fun `正文里出现指令字样不算命令`() {
        // 台词里说到斜杠不该被劫持，否则角色一说路径就换场地
        assertEquals(
            GroupChatSceneCommand.NotACommand,
            GroupChatScenes.parseCommand("他说「/去 厨房」，然后走了")
        )
    }

    @Test
    fun `只有前缀没有场景名不算命令`() {
        assertEquals(
            GroupChatSceneCommand.NotACommand,
            GroupChatScenes.parseCommand("/去 ")
        )
    }

    @Test
    fun `按名称查找支持包含匹配`() {
        val scenes = listOf(
            GroupChatScene(id = "a", name = "厨房"),
            GroupChatScene(id = "b", name = "地下室")
        )
        assertEquals("a", GroupChatScenes.findByName(scenes, "厨房")?.id)
        assertEquals("b", GroupChatScenes.findByName(scenes, "地下")?.id)
        assertNull(GroupChatScenes.findByName(scenes, "阳台"))
    }

    @Test
    fun `未设置场景时不限制成员`() {
        // null 表示「不做限制」，与「场景包含所有人」是两件事
        assertNull(GroupChatScenes.allowedCharacterIds(null))
    }

    @Test
    fun `空成员列表表示全员在场`() {
        val scene = GroupChatScene(id = "a", name = "客厅", characterIds = emptyList())
        assertNull(GroupChatScenes.allowedCharacterIds(scene))
    }

    @Test
    fun `限定成员时只返回配置的人`() {
        val scene = GroupChatScene(id = "a", name = "厨房", characterIds = listOf(1L, 3L))
        assertEquals(setOf(1L, 3L), GroupChatScenes.allowedCharacterIds(scene))
    }

    @Test
    fun `未激活时不返回场景`() {
        val scenes = listOf(GroupChatScene(id = "a", name = "厨房"))
        assertNull(GroupChatScenes.active(scenes, ""))
        assertNull(GroupChatScenes.active(scenes, "不存在"))
        assertEquals("a", GroupChatScenes.active(scenes, "a")?.id)
    }

    @Test
    fun `序列化往返不丢字段`() {
        val scenes = listOf(
            GroupChatScene(
                id = "a",
                name = "厨房",
                description = "灶台还热着",
                characterIds = listOf(7L),
                entryPrompt = "推门进来"
            )
        )
        val restored = GroupChatScenes.decode(GroupChatScenes.encode(scenes))
        assertEquals(1, restored.size)
        assertEquals("厨房", restored[0].name)
        assertEquals("灶台还热着", restored[0].description)
        assertEquals(listOf(7L), restored[0].characterIds)
    }

    @Test
    fun `坏掉的 JSON 不会让群聊打不开`() {
        assertTrue(GroupChatScenes.decode("{").isEmpty())
        assertTrue(GroupChatScenes.decode("").isEmpty())
        assertTrue(GroupChatScenes.decode(null).isEmpty())
    }

    @Test
    fun `没有名字的场景被丢弃`() {
        // 名字是唯一的人工标识，没有名字的场景在界面上无法点选
        val json = """[{"id":"a","name":""},{"id":"b","name":"阳台"}]"""
        val scenes = GroupChatScenes.decode(json)
        assertEquals(1, scenes.size)
        assertEquals("阳台", scenes[0].name)
    }

    @Test
    fun `旧数据缺 id 时补出稳定标识`() {
        val json = """[{"name":"阁楼"}]"""
        val scenes = GroupChatScenes.decode(json)
        assertEquals(1, scenes.size)
        assertTrue(scenes[0].id.isNotBlank())
        // 同样的输入应得到同样的 id，否则每次读库都变
        assertEquals(scenes[0].id, GroupChatScenes.decode(json)[0].id)
    }
}
