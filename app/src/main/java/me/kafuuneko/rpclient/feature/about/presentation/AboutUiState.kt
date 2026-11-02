package me.kafuuneko.rpclient.feature.about.presentation

/** 关于页展示的外部联系与项目信息。 */
data class AboutUiState(
    /** 关于页面展示的应用版本名称。 */
    val appInfo: AboutAppInfoState,
    /** 上游仓库地址。 */
    val githubRepoUrl: String,
    /** 上游仓库展示名。 */
    val githubRepoName: String,
    /** 上游作者联系邮箱。 */
    val developerEmail: String,
    /** 上游仓库的问题反馈地址。 */
    val githubIssuesUrl: String = "$githubRepoUrl/issues",
    /** 本定制分支的仓库地址。 */
    val forkRepoUrl: String = "",
    /** 本定制分支的仓库展示名。 */
    val forkRepoName: String = "",
    /** 本定制分支作者邮箱。 */
    val forkEmail: String = "",
    /** 本定制分支的问题反馈地址。 */
    val forkIssuesUrl: String = ""
)

/** 品牌标识与应用信息分组共用的安装包元数据。 */
data class AboutAppInfoState(
    val versionName: String,
    val buildNumber: String,
    val isDevelopmentBuild: Boolean
)
