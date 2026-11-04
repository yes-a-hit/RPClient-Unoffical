package me.kafuuneko.rpclient.libs.experimental

import androidx.annotation.StringRes
import me.kafuuneko.rpclient.R

/**
 * 一处实验性功能的登记项。
 *
 * 为什么要有这一层：有些功能一旦出错，坏的不是布局而是「角色说了不该说的话」，
 * 或者「存档里多出一份自己都解释不清的状态」。这类东西直接摆进设置页，
 * 用户会当成正式能力去依赖；出了事又很难说清是哪一个开关导致的。
 *
 * 所以它们统一收在这里：默认关闭、显式列出风险、坏了自己关掉就能退回原状。
 * 这一层同时也是插件接口的形状——以后外部模块想接进来，
 * 提供的就是一个 [ExperimentalFeature] 加上自己的实现，不需要改主流程。
 *
 * @property key 存进偏好设置的稳定标识，**改了就丢用户的选择**，别动
 * @property risk 出错时的影响面，决定 UI 上标什么颜色
 * @property available false 表示只登记了设想、还没实现，界面上显示成占位
 */
enum class ExperimentalFeature(
    val key: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val risk: ExperimentalRisk,
    val available: Boolean = true,
) {

    /**
     * 用关键字自动识别「玩家想去哪」，命中就换场。
     *
     * 这条以前试过、被否掉一次：一句话里出现地名太容易误判，
     * 角色说「厨房里好像有动静」不该把整场搬去厨房。
     * 现在把它做成默认关闭的开关，让愿意承担误判的人自己开。
     */
    SceneKeywordSwitch(
        key = "scene_keyword_switch",
        titleRes = R.string.experimental_scene_keyword_title,
        descriptionRes = R.string.experimental_scene_keyword_desc,
        risk = ExperimentalRisk.High,
    ),

    /**
     * 一个角色多套状态，靠记录、关键字或情节触发切换。
     *
     * 只登记、未实现：真正做起来要动角色卡结构、提示词拼装和历史回放，
     * 任何一处写错都会让存档里的角色前后对不上。先占个位置，
     * 让开关和隔离机制先就位。
     */
    CharacterStateMachine(
        key = "character_state_machine",
        titleRes = R.string.experimental_character_state_title,
        descriptionRes = R.string.experimental_character_state_desc,
        risk = ExperimentalRisk.High,
        available = false,
    ),
}

/** 实验功能出错时的影响面。UI 按这个上色，不做数值运算。 */
enum class ExperimentalRisk {
    /** 只影响观感，坏了也看得懂 */
    Low,

    /** 影响行为，但可以关掉重来 */
    Medium,

    /** 会写进存档或改变角色认知，回滚不彻底 */
    High,
}
