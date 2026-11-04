package me.kafuuneko.rpclient.libs.sample

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

/**
 * 「一份带注释的完整范本」的生成器。
 *
 * JSON 本身没有注释语法，所以每个字段旁边放一个同名的 `_字段名` 键来说明它——
 * 这样文件仍是合法 JSON，能被任意工具解析、能直接丢给模型读，
 * 同时人打开也看得懂每一栏该填什么。
 *
 * 范本覆盖四类数据：角色卡、带场景的群聊、世界书、冒险剧本（剧本包）。
 * 字段与 `libs/room/entity`、`libs/script/ScriptPackModels.kt` 一一对应，
 * 改结构时这份文件要跟着改，否则它会变成最坏的一种文档——看着权威，实际过期。
 */
object SampleDocument {

    /** 生成范本文本。 */
    fun build(version: String): String {
        val root = JsonObject()
        root.addDoc(
            "说明",
            JsonPrimitive("RPClient UV 数据格式范本，版本 " + version),
            "这份文件用来说明各个数据长什么样、每一栏是干什么用的。" +
                "它不会被自动导入——把你需要的那一段抄进自己的文件，或者给自己的角色卡/剧本做参照。"
        )
        root.add(
            "_阅读方式",
            JsonArray().apply {
                add(JsonPrimitive("每个字段旁边都有一个以 _ 开头、同名的键，那是这一栏的说明，不是数据。"))
                add(JsonPrimitive("填自己的东西时，把所有 _ 开头的键删掉即可，程序本来也不读它们。"))
                add(JsonPrimitive("空字符串、空数组表示「没有/不限制」，和省略该字段等价的是多数情况，但少数默认值非空，见各栏说明。"))
                add(JsonPrimitive("文中出现的 123、1 之类的 ID 都是占位数字，实际填你自己数据里的 ID。"))
            }
        )
        root.add("角色", characterSection())
        root.add("群聊", groupChatSection())
        root.add("世界书", worldbookSection())
        root.add("冒险", adventureSection())
        return GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(root)
    }

    /* ==================== 角色 ==================== */

    private fun characterSection(): JsonObject = JsonObject().apply {
        addDoc("说明", JsonPrimitive("单张角色卡。对应 Character Card V2，另有本应用自己的扩展档案。"), "")
        put("name", "小满")
        doc("name", "角色名。列表里显示的就是它；剧本按名字匹配本地角色卡，所以改名会让剧本找不到人。")
        put("avatar", "3f2a9c1e-0000-4c11-9a7b-000000000001")
        doc("avatar", "头像文件的 UUID，来自 files 表。手写时留空字符串即可，之后在编辑页选图。")
        putJson("characterTags", """["测试角色","温柔"]""")
        doc("characterTags", "标签，JSON 数组字符串。用于列表筛选和按标签分组。")
        put("description", "二十二岁，在城郊一家旧书店看店。说话轻，习惯先听完再回答。")
        doc("description", "外观、身份、经历的正文。这是模型最依赖的一栏，写具体的细节比堆形容词有用。")
        put("creatorNotes", "这张卡是用来测试长对话的，别当真。")
        doc("creatorNotes", "作者备注。默认不进提示词，只有模板里写了 {{charCreatorNotes}} 才会出现。")
        put("personality", "慢热，克制，对熟人话多。")
        doc("personality", "性格与行为倾向。")
        put("scenario", "深夜的书店，只剩两个人。")
        doc("scenario", "这一场戏的处境。单聊里它代替不了聊天记录，只是给模型一个起始环境。")
        put("firstMessages", "（她把最后一摞书放回架上，回头看你）还没走？")
        doc("firstMessages", "开场白。多条用 <START> 分隔，第一句是主开场白。")
        putJson("alternateGreetings", """["（灯已经关了一半）你来得正好，帮我拿一下那盏台灯。"]""")
        doc("alternateGreetings", "备用开场白，JSON 数组字符串。建会话时可以挑一条。")
        put("examplesOfDialogue", "{{user}}: 你每天都这么晚？\n{{char}}: 习惯了。<START>{{user}}: 怕黑吗？\n{{char}}: 不怕黑，怕安静。")
        doc("examplesOfDialogue", "对话示例。用 <START> 分隔不同轮次，写 {{char}} / {{user}} 而不是真实名字，换设备也不会失效。")
        put("postHistoryInstructions", "保持简短，不要替 {{user}} 说话。")
        doc("postHistoryInstructions", "后置提示词，插在聊天记录之后、模型开口之前。放格式和口吻的硬要求最有效。")
        put("systemPrompt", "")
        doc("systemPrompt", "角色级主提示词覆盖。留空用全局那份；写 {{original}} 可以引用全局提示词。")
        put("creator", "yes a hit")
        doc("creator", "作者，仅作元数据。")
        put("characterVersion", "1.0")
        doc("characterVersion", "角色卡版本号，仅作元数据。")
        put("depthPromptPrompt", "她记得你说过喜欢下雨。")
        doc("depthPromptPrompt", "Character's Note 的正文，按 depthPromptDepth 指定的深度插进聊天记录里。")
        put("depthPromptDepth", 4)
        doc("depthPromptDepth", "插入深度：0 表示聊天末尾，1 表示最后一条消息之前，数字越大插得越靠前。")
        put("depthPromptRole", 0)
        doc("depthPromptRole", "以谁的身份插入：0=系统，1=用户，2=角色。")
        put("characterLorebookId", 0)
        doc("characterLorebookId", "绑定的世界书 ID；0 表示不绑定。导入带 character_book 的角色卡时会自动建一本。")
        add(
            "extensionsJson",
            JsonObject().apply {
                addDoc(
                    "说明",
                    JsonPrimitive("原样保留的第三方 extensions，本应用的私有数据也放这里。"),
                    "没有要保留的内容给一个 {} 就行。"
                )
                add(
                    "rpclient_profile",
                    JsonObject().apply {
                        addDoc(
                            "说明",
                            JsonPrimitive("本应用的扩展档案。"),
                            "填了才会进提示词；全部留空等于没写。"
                        )
                        put("age", "22")
                        doc("age", "年龄。")
                        put("gender", "女")
                        doc("gender", "性别。")
                        put("occupation", "旧书店店员")
                        doc("occupation", "职业。")
                        put("orientation", "双性恋")
                        doc("orientation", "性向。")
                        put("relationshipStatus", "single")
                        doc(
                            "relationshipStatus",
                            "关系状态，只能填这几个值之一：unspecified / single / dating / married / divorced / widowed / complicated。"
                        )
                        put("hasRomanticHistory", true)
                        doc("hasRomanticHistory", "有没有感情经历。true / false；不确定就整个键删掉。")
                        put("romanticHistory", "大学时谈过一段，毕业时散了。")
                        doc("romanticHistory", "感情经历的正文。")
                        put("hasSexualHistory", false)
                        doc("hasSexualHistory", "有没有性经历。true / false。")
                        put("sexualHistory", "")
                        doc("sexualHistory", "性经历的正文。这一栏和其他栏一样只是设定，写不写、写多少由你定。")
                        put("boundaries", "不谈家人，不回答关于失踪的事。")
                        doc("boundaries", "禁区。模型被要求绕开这里写的内容。")
                        putJson("likes", """["雨声","旧纸的味道"]""")
                        doc("likes", "喜欢的东西，JSON 数组。")
                        putJson("dislikes", """["电话铃声"]""")
                        doc("dislikes", "讨厌的东西，JSON 数组。")
                        put("extraNotes", "紧张时会反复擦柜台。")
                        doc("extraNotes", "上面几栏装不下的补充。")
                    }
                )
            }
        )
        doc("extensionsJson", "第三方扩展的原样保留区。本应用的扩展档案放在它下面的 rpclient_profile 里。")
    }

    /* ==================== 群聊 ==================== */

    private fun groupChatSection(): JsonObject = JsonObject().apply {
        addDoc(
            "说明",
            JsonPrimitive("一个群聊会话，含场景配置。"),
            "成员列表、消息、摘要各自存在别的表里，这里只有会话级设置。"
        )
        put("title", "20261103-1547")
        doc("title", "会话标题，列表里显示的。")
        put("userName", "")
        doc("userName", "你在本群的名字。留空跟随全局设置。")
        put("userDescription", "")
        doc("userDescription", "你在本群的人设。留空跟随全局设置。")
        put("scenario", "凌晨的书店，停电了。")
        doc("scenario", "群聊级的共同处境，会叠加在每个角色的场景设定之上。")
        put("userNote", "轻小说风格，节奏慢一点。")
        doc("userNote", "作者注释。比 scenario 更偏「怎么写」而不是「在哪」。")
        putJson("lorebookEntrySet", "[12,13]")
        doc("lorebookEntrySet", "本群启用的世界书条目 ID，JSON 数组字符串。条目 ID 全局唯一，不分属哪本书。")
        put("systemPromptOverride", "")
        doc("systemPromptOverride", "群聊主提示词覆盖。留空用全局。")
        put("groupNudgePromptOverride", "")
        doc("groupNudgePromptOverride", "多人发言引导提示词覆盖。留空用全局。")
        put("newGroupChatPromptOverride", "")
        doc("newGroupChatPromptOverride", "开场提示词覆盖。留空用全局。")
        put("activationStrategy", "Natural")
        doc(
            "activationStrategy",
            "下一个谁说话：Manual 你点谁谁答 / Natural 按情境自然挑 / List 按名单轮流 / Pooled 在候选里随机。"
        )
        put("allowSelfResponses", false)
        doc("allowSelfResponses", "允许刚说完的角色紧接着再说一次。")
        put("characterCardMode", "Swap")
        doc("characterCardMode", "多张角色卡怎么进提示词：Swap 只放当前发言者（省 token）/ Join 全部放进去。")
        put("includeMutedCards", false)
        doc("includeMutedCards", "被闭麦的角色卡是否仍进提示词。")
        put("autoModeEnabled", false)
        doc("autoModeEnabled", "自动连续生成，不用你每次都点。")
        put("trimOtherSpeakers", true)
        doc("trimOtherSpeakers", "裁掉角色回复里替别人写的那部分发言。")
        put("autoSummaryPaused", false)
        doc("autoSummaryPaused", "暂停自动摘要。手动摘要不受影响。")
        add(
            "scenes",
            JsonArray().apply {
                add(
                    JsonObject().apply {
                        addDoc(
                            "说明",
                            JsonPrimitive("一个场景：此刻谁在这、只用哪些设定。"),
                            "场景是「收窄」不是「替换」——配了才生效，没配过的旧行为完全不变。"
                        )
                        put("id", "scene-kitchen")
                        doc("id", "场景标识，会话内唯一。切换场景时存的是它。")
                        put("name", "厨房")
                        doc("name", "场景名，顶栏和「+」菜单里显示的就是它。")
                        put("description", "灶上还温着一锅汤。")
                        doc("description", "场景设定，进入提示词，叠加在群聊 scenario 之上。")
                        putJson("characterIds", "[1,2]")
                        doc("characterIds", "驻扎在这里的角色 ID。空数组表示全员都在。")
                        put("entryPrompt", "（油烟机停了，只剩汤在响）")
                        doc("entryPrompt", "切进来时补一条旁白。可以留空。")
                        putJson("lorebookEntryIds", "[12]")
                        doc("lorebookEntryIds", "本场只用的世界书条目 ID。空数组表示不干预，沿用会话启用的那批。")
                    }
                )
            }
        )
        doc("scenes", "场景列表。数组为空等于没配场景，行为与旧版完全一致。")
        put("activeSceneId", "")
        doc("activeSceneId", "当前生效的场景 ID。留空表示不限场景、全员在场。")
    }

    /* ==================== 世界书 ==================== */

    private fun worldbookSection(): JsonObject = JsonObject().apply {
        addDoc("说明", JsonPrimitive("一本世界书和它的条目。"), "条目靠关键词命中才注入，常驻条目例外。")
        put("name", "书店设定")
        doc("name", "世界书名。")
        put("description", "这家店的来历。")
        doc("description", "说明文字，仅用于管理，不进提示词。")
        put("scanDepth", 2)
        doc("scanDepth", "默认只扫描最近几条消息找关键词。单个条目可以覆盖。")
        put("tokenBudget", 0)
        doc("tokenBudget", "本书固定 Token 上限。0 表示跟随全局预算、不额外限制。")
        put("recursiveScanning", false)
        doc("recursiveScanning", "已命中条目的正文是否继续参与下一轮关键词扫描。")
        add(
            "entries",
            JsonArray().apply {
                add(
                    JsonObject().apply {
                        addDoc("说明", JsonPrimitive("一条设定。"), "字段和 SillyTavern 的 world info 基本对齐。")
                        put("name", "地下室")
                        doc("name", "条目名，便于管理。")
                        putJson("keywords", """["地下室","地窖"]""")
                        doc("keywords", "主关键词，JSON 数组字符串。命中任意一个就激活。")
                        putJson("secondaryKeywords", """[]""")
                        doc("secondaryKeywords", "次要关键词，配合 selectiveLogic 使用。")
                        put("selectiveLogic", 0)
                        doc(
                            "selectiveLogic",
                            "次要关键词怎么判：0=任一命中 / 1=非全部 / 2=一个都不 / 3=全部命中。"
                        )
                        put("constant", false)
                        doc("constant", "常驻激活：不看关键词，每次都进提示词。")
                        put("order", 100)
                        doc("order", "插入优先级，数值越小越靠前。")
                        put("position", 4)
                        doc(
                            "position",
                            "插入位置：0=聊天记录前 / 1=聊天记录后 / 2=作者注释顶 / 3=作者注释底 / 4=按深度插入 / 5=示例顶 / 6=示例底 / 7=插槽。"
                        )
                        put("depth", 4)
                        doc("depth", "position=4 时的深度：0 表示末尾，1 表示最后一条消息之前，数字越大越靠前。")
                        put("role", 0)
                        doc("role", "以谁的身份注入：0=系统，1=用户，2=角色。")
                        put("content", "地下室的门锁着，钥匙在前台抽屉第二格。")
                        doc("content", "激活后真正注入的正文。这是这条设定唯一有信息量的部分。")
                        put("category", "[]")
                        doc("category", "分类标签，JSON 数组字符串，仅用于管理。")
                        put("disabled", false)
                        doc("disabled", "禁用后即使命中也不注入。")
                        put("probability", 100)
                        doc("probability", "命中后再按这个百分比抽一次。100 表示必中。")
                        put("ignoreBudget", false)
                        doc("ignoreBudget", "不计入 Token 预算，永远不会被裁剪。")
                        putNullable("scanDepth", null)
                        doc("scanDepth", "本条目自己的扫描深度，null 表示继承世界书设置。")
                        putNullable("matchWholeWords", null)
                        doc("matchWholeWords", "整词匹配。null 表示跟随全局开关。")
                        putNullable("caseSensitive", null)
                        doc("caseSensitive", "大小写敏感。null 或 false 都表示忽略大小写。")
                        put("useGroupScoring", false)
                        doc("useGroupScoring", "同组条目之间做评分，只选分数最高的。")
                        put("group", "")
                        doc("group", "互斥组名。同名的条目同一时刻只进一条。")
                        put("groupOverride", false)
                        doc("groupOverride", "命中后强制压过同组其他条目。")
                        putNullable("groupWeight", null)
                        doc("groupWeight", "组内随机权重，null 表示用默认值。")
                        put("preventRecursion", false)
                        doc("preventRecursion", "被激活后不再参与后续递归扫描。")
                        put("delayUntilRecursion", false)
                        doc("delayUntilRecursion", "只允许递归扫描触发，首轮不触发。")
                        putNullable("sticky", null)
                        doc("sticky", "激活后保持多少轮，期间不看关键词也继续注入。null 表示不保持。")
                        putNullable("cooldown", null)
                        doc("cooldown", "保持结束后的冷却轮数。")
                        putNullable("delay", null)
                        doc("delay", "聊天至少进行到第几轮才允许触发。")
                        put("outletName", "")
                        doc("outletName", "position=7 时的插槽名，对应模板里的 {{outlet::名字}}。")
                        putJson("triggers", "[]")
                        doc("triggers", "允许触发的生成类型，空数组表示全部允许。")
                        put("matchCharacterDescription", false)
                        doc("matchCharacterDescription", "把角色描述也纳入扫描源。")
                        put("matchCharacterPersonality", false)
                        doc("matchCharacterPersonality", "把性格设定纳入扫描源。")
                        put("matchScenario", false)
                        doc("matchScenario", "把场景设定纳入扫描源。")
                        put("matchCreatorNotes", false)
                        doc("matchCreatorNotes", "把作者备注纳入扫描源。")
                        put("matchPersonaDescription", false)
                        doc("matchPersonaDescription", "把你的人设描述纳入扫描源。")
                        put("matchCharacterDepthPrompt", false)
                        doc("matchCharacterDepthPrompt", "把 Character's Note 纳入扫描源。")
                    }
                )
            }
        )
        doc("entries", "条目列表。")
    }

    /* ==================== 冒险 ==================== */

    private fun adventureSection(): JsonObject = JsonObject().apply {
        addDoc(
            "说明",
            JsonPrimitive("一个剧本包（冒险模式）。"),
            "设定与流程分开：世界书管设定，场景顺序管流程。运行时只按 mode 解释，不写死状态机。"
        )
        put("formatVersion", 1)
        doc("formatVersion", "结构版本，当前是 1。")
        put("id", "demo-three-nights")
        doc("id", "剧本唯一标识，导入时用它去重。换内容不换 id，会覆盖旧的。")
        put("name", "三夜")
        doc("name", "剧本名。")
        put("description", "一间值班室，四个玩偶。")
        doc("description", "简介，列表里显示。")
        put("mode", "Mainline")
        doc("mode", "Mainline 主线按场景推进 / Loop 可重复直到通关 / Free 自由，不限制顺序。")
        put("startSceneId", "night1")
        doc("startSceneId", "起始场景 ID。")
        putJson("startSceneIds", "[]")
        doc("startSceneIds", "多入口时把所有起始场景 ID 写这里，单入口留空数组。")
        put("showDashboard", true)
        doc("showDashboard", "游玩页是否显示状态面板。关掉的话变量照常算，只是不露给玩家。")
        put("freeModeAfterClear", true)
        doc("freeModeAfterClear", "通关后是否转自由模式，让玩家继续玩。")
        put("enableGm", true)
        doc("enableGm", "启用 GM 叙述层。")
        put("enableDice", true)
        doc("enableDice", "启用骰子判定。")
        put("backgroundKey", "")
        doc("backgroundKey", "剧本级背景图的图库标签。留空则各场景自己按名字找。")
        put("showCompletion", true)
        doc("showCompletion", "通关条件是否对玩家可见。")
        put("shareSettings", false)
        doc("shareSettings", "设定是否允许其他玩家查看。")
        add(
            "openings",
            JsonArray().apply {
                add(
                    JsonObject().apply {
                        put("speaker", "")
                        doc("speaker", "谁说的。空字符串表示旁白。")
                        put("content", "值班室的钟停在 12:00。")
                        doc("content", "开场白正文。按顺序作为开局消息播种。")
                    }
                )
            }
        )
        doc("openings", "开场白列表。可以没有，也可以有多条。")
        add(
            "variables",
            JsonArray().apply {
                add(
                    JsonObject().apply {
                        addDoc(
                            "说明",
                            JsonPrimitive("一个剧本变量。"),
                            "场景解锁、事件触发、成败判定都能拿它当条件。"
                        )
                        put("name", "电量")
                        doc("name", "变量名，条件与副作用里按它引用。改名会让引用它的地方全部失效。")
                        put("initial", 100)
                        doc("initial", "初始值。开关类变量用 0/1。")
                        put("min", 0)
                        doc("min", "下限，运行时钳制。")
                        put("max", 100)
                        doc("max", "上限，运行时钳制。")
                        put("label", "电量")
                        doc("label", "面板上显示的名字。留空直接用 name。")
                        put("visible", true)
                        doc("visible", "是否露给玩家。藏起来可以做隐藏好感度。")
                        put("kind", "Number")
                        doc("kind", "Number 数字 / Switch 开关 / Text 文本状态 / Note 纯说明。")
                        put("text", "")
                        doc("text", "Text 类型的初始内容，例如「邦尼 · 左走廊」。")
                        put("group", "全局")
                        doc("group", "面板分组名。全局放跨场景的量，本场放这一场才关心的。")
                        put("onLabel", "")
                        doc("onLabel", "Switch 的开启文案。留空显示「是」。")
                        put("offLabel", "")
                        doc("offLabel", "Switch 的关闭文案。留空显示「否」。")
                        put("resetOnRestart", true)
                        doc("resetOnRestart", "重开时是否回到初始值。结局解锁这类要跨周目保留的设成 false。")
                    }
                )
            }
        )
        doc("variables", "剧本级数值变量。")
        add(
            "globalGuardRules",
            JsonArray().apply { add(guardRuleSample()) }
        )
        doc("globalGuardRules", "整局生效的越界拦截规则。场景级规则排在它后面。")
        add(
            "globalLorebook",
            JsonObject().apply {
                put("name", "值班室")
                doc("name", "世界书名。导入后会落成一本普通世界书。")
                add(
                    "entries",
                    JsonArray().apply {
                        add(
                            JsonObject().apply {
                                put("name", "手电")
                                doc("name", "条目名。")
                                put("content", "手电只能逼退，不能破坏。")
                                doc("content", "正文。")
                                putJson("keys", """["手电","照"]""")
                                doc("keys", "触发关键词。")
                                put("constant", false)
                                doc("constant", "常驻注入。开启后不看关键词。")
                            }
                        )
                    }
                )
                doc("entries", "设定条目。")
            }
        )
        doc("globalLorebook", "全局世界书，整局都参与提示词。")
        add(
            "scenes",
            JsonArray().apply {
                add(
                    JsonObject().apply {
                        addDoc(
                            "说明",
                            JsonPrimitive("一个场景。"),
                            "场景顺序即推荐推进顺序；nextSceneIds 为空表示这里是终点。"
                        )
                        put("id", "night1")
                        doc("id", "场景 ID，被 nextSceneIds / gotoSceneId 引用。")
                        put("name", "第一夜")
                        doc("name", "场景名。")
                        put("entryPrompt", "值班室里有四扇门。")
                        doc("entryPrompt", "进入时插一次的开场描述。")
                        putJson("nextSceneIds", """["night2"]""")
                        doc("nextSceneIds", "后继场景，按顺序尝试。空数组表示本场是终点。")
                        put("loopEnabled", false)
                        doc("loopEnabled", "允许重复进入。")
                        put("checkpoint", false)
                        doc("checkpoint", "作为失败回滚的检查点。开启后会记录变量与在场状态，失败可回到这里重来。")
                        put("maxRounds", 0)
                        doc("maxRounds", "本场最多几轮对话。0 表示不限。")
                        put("failText", "")
                        doc("failText", "轮次用尽时的提示。留空用默认文案。")
                        put("timeoutSceneId", "")
                        doc("timeoutSceneId", "轮次用尽后转到哪个场景。留空表示直接收官。")
                        putJson("dashboardItems", """["电量","左门状态"]""")
                        doc("dashboardItems", "本场面板只显示这些变量。空数组表示按变量自己的 visible 走。")
                        putJson("cast", """[{"name":"霍斯","required":true,"description":""}]""")
                        doc("cast", "允许出场的角色，按名字匹配本地角色卡；找不到就按 description 自动建一张。")
                        putJson("conditionalCast", """[{"name":"邦尼","condition":{"type":"Variable","variable":"夜数","threshold":2,"comparison":"AtLeast"},"description":""}]""")
                        doc("conditionalCast", "按状态决定是否到场的角色。condition 为空表示总是到场。")
                        putJson("bannedCast", """[]""")
                        doc("bannedCast", "禁止出场的角色名，优先级高于 cast。")
                        putJson("suggestedActions", """["拿起手电","看向左门"]""")
                        doc("suggestedActions", "作者预设的候选行动。有预设就直接展示，不等模型输出。")
                        putJson("unlockedBy", """[]""")
                        doc("unlockedBy", "前置场景：这些场景都经历过才解锁。空数组表示一开始就可达。")
                        put("inheritBackground", true)
                        doc("inheritBackground", "是否继承剧本级背景。关掉且本场没指定，表示这一场就是不要背景。")
                        put("backgroundKey", "")
                        doc("backgroundKey", "本场背景图的图库标签。")
                        add(
                            "completionCondition",
                            JsonObject().apply {
                                put("type", "RoundLimit")
                                doc("type", "Keyword 关键词 / Regex 正则 / RoundLimit 轮数 / ScoreAtLeast 评分 / Variable 变量。")
                                put("pattern", "")
                                doc("pattern", "Keyword 与 Regex 用。多个说法用 | 分隔。")
                                put("threshold", 8)
                                doc("threshold", "轮数或数值阈值。")
                                put("variable", "")
                                doc("variable", "Variable 类型用的变量名。")
                                put("comparison", "AtLeast")
                                doc("comparison", "AtLeast 达到 / AtMost 低到 / Equal 正好等于。")
                                put("description", "撑到早上六点。")
                                doc("description", "给玩家看的说明。")
                            }
                        )
                        doc("completionCondition", "通关条件。命中即判定本场完成。")
                        add(
                            "failConditions",
                            JsonArray().apply {
                                add(
                                    JsonObject().apply {
                                        put("type", "Variable")
                                        doc("type", "同上五种类型。")
                                        put("pattern", "")
                                        doc("pattern", "Keyword 与 Regex 用。")
                                        put("threshold", 0)
                                        doc("threshold", "阈值。电量归零就是 Variable + 电量 + AtMost + 0。")
                                        put("variable", "电量")
                                        doc("variable", "变量名。")
                                        put("comparison", "AtMost")
                                        doc("comparison", "比较方向。")
                                        put("failSceneId", "")
                                        doc("failSceneId", "命中后转到的惩罚场景。留空表示直接结束整局。")
                                        put("description", "没电了。")
                                        doc("description", "给玩家看的失败说明。")
                                    }
                                )
                            }
                        )
                        doc("failConditions", "失败条件列表。任意一条命中即判本场失败。")
                        add(
                            "events",
                            JsonArray().apply {
                                add(
                                    JsonObject().apply {
                                        put("id", "fox-arrives")
                                        doc("id", "事件标识，用于记录是否已触发。")
                                        put("name", "霍斯出现")
                                        doc("name", "展示名，也用作触发时旁白的默认文案。")
                                        put("atRound", 3)
                                        doc("atRound", "第几轮触发。0 表示不按轮次，改为靠 condition。")
                                        put("narration", "走廊尽头传来金属摩擦声。")
                                        doc("narration", "触发时插入的旁白。留空用 name。")
                                        putJson("addCast", """["霍斯"]""")
                                        doc("addCast", "触发时追加到在场名单的角色。")
                                        putJson("removeCast", """[]""")
                                        doc("removeCast", "触发时移出在场名单的角色。")
                                        put("gotoSceneId", "")
                                        doc("gotoSceneId", "触发后强制转到的场景。留空留在原地。")
                                        put("once", true)
                                        doc("once", "只触发一次。关掉则每次条件满足都触发。")
                                        put("chance", 100)
                                        doc("chance", "触发概率百分比。")
                                        add(
                                            "effects",
                                            JsonArray().apply {
                                                add(
                                                    JsonObject().apply {
                                                        put("variable", "电量")
                                                        doc("variable", "要改的变量名。")
                                                        put("delta", -10)
                                                        doc("delta", "增量，可为负。写了 setTo 就不看它。")
                                                        putNullable("setTo", null)
                                                        doc("setTo", "直接赋值。null 表示按 delta 算。")
                                                        put("setText", "")
                                                        doc("setText", "文本变量的新内容。填了就不按数值处理。")
                                                        put("appendText", "")
                                                        doc("appendText", "往文本变量末尾追加一条，用于攒线索。")
                                                    }
                                                )
                                            }
                                        )
                                        doc("effects", "触发时对变量的改动。")
                                    }
                                )
                            }
                        )
                        doc("events", "场景内的触发事件。")
                        add(
                            "guardRules",
                            JsonArray().apply { add(guardRuleSample()) }
                        )
                        doc("guardRules", "本场的越界拦截规则，与剧本级的合并生效，先命中先算。")
                    }
                )
            }
        )
        doc("scenes", "场景列表。")
    }

    /** 越界拦截规则示例，剧本级和场景级共用同一形状。 */
    private fun guardRuleSample(): JsonObject = JsonObject().apply {
        addDoc(
            "说明",
            JsonPrimitive("一条越界拦截规则。"),
            "判定发生在回复落库之前：命中就把这一轮作废，玩家看不到那段文字，模型也拿不到它当上下文。"
        )
        put("pattern", "打死|崩了|杀掉|击毙")
        doc("pattern", "匹配片段。regex 为假时用 | 分隔多个说法，为真时整段当正则。")
        put("regex", false)
        doc("regex", "按正则匹配。")
        put("description", "玩偶不是普通手段能放倒的东西。")
        doc("description", "命中后给玩家看的一句话。留空会退化成通用文案。")
        put("hardFail", false)
        doc("hardFail", "命中即判本场失败，而不是只作废这一轮。")
        put("failSceneId", "")
        doc("failSceneId", "hardFail 为真时转到哪个场景。留空表示直接收官。")
        put("retryHint", "他扑上来，你只有手电；写他被逼退，不要写他倒下。")
        doc("retryHint", "提示模型重写时要绕开什么。空着的话模型多半会原样再写一遍。")
    }

    /* ==================== 小工具 ==================== */

    /** 放一个字符串字段。 */
    private fun JsonObject.put(key: String, value: String) {
        add(key, JsonPrimitive(value))
    }

    /** 放一个整数字段。 */
    private fun JsonObject.put(key: String, value: Int) {
        add(key, JsonPrimitive(value))
    }

    /** 放一个布尔字段。 */
    private fun JsonObject.put(key: String, value: Boolean) {
        add(key, JsonPrimitive(value))
    }

    /**
     * 放一个可以缺省的数值字段。
     *
     * 和上面的 [put] 分开命名是因为 `put(key, null)` 会让编译器在
     * String 版和 Int 版之间挑不出来；JSON 里 null 与「不写」语义不同，
     * 所以这里必须显式写出来，不能省。
     */
    private fun JsonObject.putNullable(key: String, value: Int?) {
        add(key, if (value == null) com.google.gson.JsonNull.INSTANCE else JsonPrimitive(value))
    }

    /** 放一个原本就是 JSON 文本的字段（本项目大量字段是「JSON 数组字符串」）。 */
    private fun JsonObject.putJson(key: String, json: String) {
        val element = runCatching {
            com.google.gson.JsonParser.parseString(json)
        }.getOrNull()
        add(key, element ?: JsonPrimitive(json))
    }

    /** 给 [key] 配一条说明，写成 `_key`。 */
    private fun JsonObject.doc(key: String, comment: String) {
        add("_" + key, JsonPrimitive(comment))
    }

    /** 放一个字段并同时给说明。 */
    private fun JsonObject.addDoc(key: String, value: JsonElement, comment: String) {
        add(key, value)
        if (comment.isNotEmpty()) doc(key, comment)
    }
}
