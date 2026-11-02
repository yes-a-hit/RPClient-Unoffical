package me.kafuuneko.rpclient.libs.poke

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

/**
 * 一条可以在「+」菜单里点一下就对角色做的动作。
 *
 * [template] 里的 `{name}` 会被换成对象的名字，和原来「戳一戳」的写法一致；
 * 这句话是用户发的，所以用「我」的口吻写。
 */
data class PokeAction(
    val id: String,
    val label: String,
    val template: String
)

/**
 * 动作预设的读写。
 *
 * 存成 JSON 字符串放在 `AppModel.pokeActions` 里。三项内置动作只是**默认值**：
 * 用户改过之后以存储的为准，删到只剩一条也不补回默认，否则「删不掉」比「删错了」更烦人。
 */
object PokeActions {

    const val KEY_POKE = "poke"
    const val KEY_HEADPAT = "headpat"
    const val KEY_CHEEK = "cheek"

    /** 没有配置过时用的三条。顺序就是菜单里的顺序。 */
    val DEFAULTS: List<PokeAction> = listOf(
        PokeAction(KEY_POKE, "戳一戳", "（戳了戳 {name}）"),
        PokeAction(KEY_HEADPAT, "摸头", "（摸了摸 {name} 的头）"),
        PokeAction(KEY_CHEEK, "捏脸", "（捏了捏 {name} 的脸）")
    )

    private val gson = Gson()

    /**
     * 解析存储值。空字符串表示「从没配置过」，返回默认三条；
     * 非空但解析失败（手改坏了）也退回默认，不让页面直接空掉。
     */
    fun parse(raw: String): List<PokeAction> {
        if (raw.isBlank()) return DEFAULTS
        return runCatching {
            val array = gson.fromJson(raw, JsonArray::class.java) ?: return DEFAULTS
            array.mapNotNull { element ->
                val obj = element.takeIf { it.isJsonObject }?.asJsonObject ?: return@mapNotNull null
                val id = obj.stringOrNull("id") ?: return@mapNotNull null
                val template = obj.stringOrNull("template") ?: return@mapNotNull null
                PokeAction(id = id, label = obj.stringOrNull("label") ?: id, template = template)
            }.ifEmpty { DEFAULTS }
        }.getOrDefault(DEFAULTS)
    }

    fun serialize(actions: List<PokeAction>): String {
        val array = JsonArray()
        actions.forEach { action ->
            array.add(JsonObject().apply {
                addProperty("id", action.id)
                addProperty("label", action.label)
                addProperty("template", action.template)
            })
        }
        return gson.toJson(array)
    }

    /** 把模板里的占位符换成真实名字。 */
    fun render(action: PokeAction, name: String): String =
        action.template.replace("{name}", name)

    private fun JsonObject.stringOrNull(key: String): String? =
        get(key)?.takeIf { it.isJsonPrimitive }?.asString?.takeIf { it.isNotBlank() }
}
