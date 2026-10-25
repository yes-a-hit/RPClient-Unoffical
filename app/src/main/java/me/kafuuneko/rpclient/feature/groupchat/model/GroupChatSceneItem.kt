package me.kafuuneko.rpclient.feature.groupchat.model

/**
 * 群聊场景标签的展示数据。
 *
 * @property memberNames 驻扎在本场的角色名，用于标签上直接看出「这一场有谁」。
 * @property restrictMembers 是否限定了成员；false 表示全员都在。
 */
data class GroupChatSceneItem(
    val id: String,
    val name: String,
    val memberNames: List<String> = emptyList(),
    val restrictMembers: Boolean = false
)
