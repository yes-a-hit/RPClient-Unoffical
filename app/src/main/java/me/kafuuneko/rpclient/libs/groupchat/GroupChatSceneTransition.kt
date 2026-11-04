package me.kafuuneko.rpclient.libs.groupchat

import me.kafuuneko.rpclient.libs.groupchat.model.GroupChatScene

/**
 * 一次场景切换在「人」这一层意味着什么。
 *
 * 场景本身只是「谁在哪儿」，但玩家从 A 挪到 B 这件事对每个人都不一样：
 * 有人跟着走了，有人被留在原地，有人本来就在目的地等着。
 * 这三拨人的反应完全不同——留下来的那个才可能吃醋，
 * 本来就在那儿的那个才可能问「你怎么来了」。
 *
 * 这一层只做纯计算，不碰数据库也不碰 UI：
 * 输入是前后两个场景和成员名单，输出是三拨人。
 */
object GroupChatSceneTransition {

    /**
     * @property from 出发场景；null 表示从「全员」状态出发
     * @property to 目标场景；null 表示回到「全员」
     * @property companions 跟着玩家一起过去的角色
     * @property leftBehind 被留在出发地的角色
     * @property alreadyThere 本来就在目的地、这次没挪窝的角色
     * @property secret 这次移动对留下的人是否可见
     */
    data class Plan(
        val from: GroupChatScene?,
        val to: GroupChatScene?,
        val companions: List<Long>,
        val leftBehind: List<Long>,
        val alreadyThere: List<Long>,
        val secret: Boolean,
    ) {
        /** 有实际位移才值得写旁白。原地不动不该刷一条消息。 */
        val hasMovement: Boolean
            get() = from?.id != to?.id && (companions.isNotEmpty() || leftBehind.isNotEmpty() || to != null)
    }

    /**
     * 算出这次切换的三拨人。
     *
     * 判据是「前后两场都在场」= 同行；「只在前一场」= 被留下；
     * 「只在后一场」= 本来就在那儿。空 [GroupChatScene.characterIds]
     * 表示这一场不限定成员，按全员处理。
     *
     * @param allMemberIds 会话里的全部角色 ID，用于展开「不限定成员」的场景
     */
    fun plan(
        from: GroupChatScene?,
        to: GroupChatScene?,
        allMemberIds: List<Long>,
    ): Plan {
        val castFrom = castOf(from, allMemberIds)
        val castTo = castOf(to, allMemberIds)
        // 涉及隐蔽场景的移动都算偷偷：从秘密地点溜回来同样是瞒着其他人的
        val secret = (to ?: from)?.visibility == me.kafuuneko.rpclient.libs.groupchat.model.SceneVisibility.Secret
        return Plan(
            from = from,
            to = to,
            companions = castFrom.intersect(castTo).toList(),
            leftBehind = (castFrom - castTo).toList(),
            alreadyThere = (castTo - castFrom).toList(),
            secret = secret,
        )
    }

    /**
     * 某一场景的实际在场名单。
     *
     * 三个参数分开而不是合并成一个 lambda：调用点都是「场景 + 成员表」，
     * 中间夹一个函数参数可读性反而差。
     */
    private fun castOf(scene: GroupChatScene?, allMemberIds: List<Long>): Set<Long> {
        if (scene == null) return allMemberIds.toSet()
        if (!scene.restrictsMembers) return allMemberIds.toSet()
        // 场景里配了已删除的角色时按「不在这里」处理，不做兜底展开
        return scene.characterIds.toSet()
    }

    /**
     * 把计算结果翻成可以直接拼进历史的旁白片段。
     *
     * 返回结构而不是现成字符串：名字要查库、模板要查语言资源，
     * 这两件事都不该塞进这个纯函数里。
     */
    fun narration(
        plan: Plan,
        nameOf: (Long) -> String,
    ): List<Part> {
        if (!plan.hasMovement) return emptyList()
        val out = mutableListOf<Part>()

        val fromName = plan.from?.name
        val toName = plan.to?.name
        val companions = plan.companions.map(nameOf).filter { it.isNotBlank() }
        val left = plan.leftBehind.map(nameOf).filter { it.isNotBlank() }
        val arrived = plan.alreadyThere.map(nameOf).filter { it.isNotBlank() }

        when {
            // 回到「全员」不是去某个具体地方，谈不上谁被留下、谁本来就在
            toName == null && fromName != null -> return listOf(Part.LeaveToEveryone(fromName))

            toName != null && fromName == null -> out += if (companions.isEmpty()) {
                Part.EnterAlone(toName)
            } else {
                Part.EnterWith(companions.joinToString("、"), toName)
            }

            toName != null && fromName != null -> out += when {
                plan.secret && companions.isEmpty() -> Part.SlipAway(fromName, toName)
                plan.secret -> Part.SlipAwayWith(companions.joinToString("、"), fromName, toName)
                companions.isEmpty() -> Part.MoveAlone(fromName, toName)
                else -> Part.MoveWith(companions.joinToString("、"), fromName, toName)
            }

            else -> return emptyList()
        }

        // 偷偷走的时候留下的人不知道你走了，写「被留下」等于当众宣布，
        // 和「隐蔽」自相矛盾；目的地的人仍然知道你到了
        if (left.isNotEmpty() && fromName != null && !plan.secret) {
            out += Part.LeftBehind(left.joinToString("、"), fromName)
        }
        if (arrived.isNotEmpty()) {
            out += Part.AlreadyThere(arrived.joinToString("、"))
        }
        return out
    }

    /** 旁白的一段。每一段对应一条语言资源键。 */
    sealed interface Part {
        data class LeaveToEveryone(val from: String) : Part
        data class EnterAlone(val to: String) : Part
        data class EnterWith(val companions: String, val to: String) : Part
        data class MoveAlone(val from: String, val to: String) : Part
        data class MoveWith(val companions: String, val from: String, val to: String) : Part
        data class SlipAway(val from: String, val to: String) : Part
        data class SlipAwayWith(val companions: String, val from: String, val to: String) : Part
        data class LeftBehind(val names: String, val from: String) : Part
        data class AlreadyThere(val names: String) : Part
    }
}
