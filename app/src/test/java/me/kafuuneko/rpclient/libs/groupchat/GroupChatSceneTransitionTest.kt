package me.kafuuneko.rpclient.libs.groupchat

import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatScene
import me.kafuuneko.rpclient.libs.groupchat.model.SceneVisibility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupChatSceneTransitionTest {

    private val names = mapOf(1L to "小满", 2L to "阿夜", 3L to "霍斯")
    private fun nameOf(id: Long): String = names[id].orEmpty()

    private fun scene(
        id: String,
        name: String,
        members: List<Long>,
        visibility: SceneVisibility = SceneVisibility.Open,
    ) = GroupChatScene(
        id = id,
        name = name,
        characterIds = members,
        visibility = visibility,
    )

    @Test
    fun `full cast to a narrowed scene leaves everyone else behind`() {
        val plan = GroupChatSceneTransition.plan(
            from = null,
            to = scene("k", "厨房", listOf(2)),
            allMemberIds = listOf(1, 2, 3),
        )
        // 从「全员」出发时，目的地的人算同行，其余都留在原地
        assertEquals(listOf(2L), plan.companions)
        assertEquals(listOf(1L, 3L), plan.leftBehind)
        assertTrue(plan.alreadyThere.isEmpty())
    }

    @Test
    fun `moving between two scenes splits companions and the left behind`() {
        val plan = GroupChatSceneTransition.plan(
            from = scene("hall", "客厅", listOf(1, 2)),
            to = scene("k", "厨房", listOf(2)),
            allMemberIds = listOf(1, 2, 3),
        )
        // 主人举的例子：A 和 B 一起，玩家只带 B 走，A 被留下
        assertEquals(listOf(2L), plan.companions)
        assertEquals(listOf(1L), plan.leftBehind)
    }

    @Test
    fun `people waiting at the destination are neither companions nor left behind`() {
        val plan = GroupChatSceneTransition.plan(
            from = scene("hall", "客厅", listOf(1)),
            to = scene("k", "厨房", listOf(2, 3)),
            allMemberIds = listOf(1, 2, 3),
        )
        assertTrue(plan.companions.isEmpty())
        assertEquals(listOf(1L), plan.leftBehind)
        assertEquals(listOf(2L, 3L), plan.alreadyThere)
    }

    @Test
    fun `staying in the same scene is not a movement`() {
        val kitchen = scene("k", "厨房", listOf(2))
        val plan = GroupChatSceneTransition.plan(
            from = kitchen,
            to = kitchen,
            allMemberIds = listOf(1, 2),
        )
        assertFalse(plan.hasMovement)
        assertTrue(GroupChatSceneTransition.narration(plan, ::nameOf).isEmpty())
    }

    @Test
    fun `open move names who came along and who stayed`() {
        val plan = GroupChatSceneTransition.plan(
            from = scene("hall", "客厅", listOf(1, 2)),
            to = scene("k", "厨房", listOf(2)),
            allMemberIds = listOf(1, 2, 3),
        )
        val parts = GroupChatSceneTransition.narration(plan, ::nameOf)
        assertEquals(
            listOf(
                GroupChatSceneTransition.Part.MoveWith("阿夜", "客厅", "厨房"),
                GroupChatSceneTransition.Part.LeftBehind("小满", "客厅"),
            ),
            parts,
        )
    }

    @Test
    fun `secret move uses the slip away wording`() {
        val plan = GroupChatSceneTransition.plan(
            from = scene("hall", "客厅", listOf(1, 2)),
            to = scene("k", "厨房", listOf(2), SceneVisibility.Secret),
            allMemberIds = listOf(1, 2, 3),
        )
        assertTrue(plan.secret)
        val parts = GroupChatSceneTransition.narration(plan, ::nameOf)
        // 偷偷走的时候不能写「小满被留在客厅」——那等于当众宣布，和「隐蔽」自相矛盾
        assertEquals(
            listOf(GroupChatSceneTransition.Part.SlipAwayWith("阿夜", "客厅", "厨房")),
            parts,
        )
    }

    @Test
    fun `secret move alone has no companions`() {
        val plan = GroupChatSceneTransition.plan(
            from = scene("hall", "客厅", listOf(1)),
            to = scene("k", "厨房", emptyList(), SceneVisibility.Secret),
            allMemberIds = listOf(1, 2),
        )
        // 目的地不限定成员表示全员都在，出发地的人全成了同行者
        assertTrue(plan.leftBehind.isEmpty())
        val parts = GroupChatSceneTransition.narration(plan, ::nameOf)
        assertEquals(GroupChatSceneTransition.Part.SlipAwayWith("小满", "客厅", "厨房"), parts.first())
    }

    @Test
    fun `leaving a secret scene on the way back still counts as secret`() {
        val plan = GroupChatSceneTransition.plan(
            from = scene("k", "卧室", listOf(2), SceneVisibility.Secret),
            to = null,
            allMemberIds = listOf(1, 2, 3),
        )
        // 从偷情的地方溜回来同样是瞒着人的，提示词据此要求在场角色别声张
        assertTrue(plan.secret)
        assertEquals(
            listOf(GroupChatSceneTransition.Part.LeaveToEveryone("卧室")),
            GroupChatSceneTransition.narration(plan, ::nameOf),
        )
    }

    @Test
    fun `returning to everyone is a single leave line`() {
        val plan = GroupChatSceneTransition.plan(
            from = scene("k", "厨房", listOf(2)),
            to = null,
            allMemberIds = listOf(1, 2, 3),
        )
        // 「全员」不是具体地点，只留一句「回到大家中间」，不追谁被留下
        assertEquals(
            listOf(GroupChatSceneTransition.Part.LeaveToEveryone("厨房")),
            GroupChatSceneTransition.narration(plan, ::nameOf),
        )
    }

    @Test
    fun `entering from nowhere reports who is already there`() {
        val plan = GroupChatSceneTransition.plan(
            from = null,
            to = null,
            allMemberIds = listOf(1),
        )
        assertFalse(plan.hasMovement)
    }

    @Test
    fun `unknown character ids do not break the narration`() {
        val plan = GroupChatSceneTransition.plan(
            from = scene("hall", "客厅", listOf(1, 99)),
            to = scene("k", "厨房", listOf(2)),
            allMemberIds = listOf(1, 2, 3),
        )
        // 99 查不到名字，只影响旁白措辞，不该抛异常
        val parts = GroupChatSceneTransition.narration(plan, ::nameOf)
        assertTrue(parts.isNotEmpty())
    }
}
