package me.kafuuneko.rpclient.feature.main.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.main.presentation.MainExperimentalState
import me.kafuuneko.rpclient.feature.main.presentation.MainUiIntent
import me.kafuuneko.rpclient.libs.experimental.ExperimentalFeature
import me.kafuuneko.rpclient.libs.experimental.ExperimentalRisk
import me.kafuuneko.rpclient.ui.widgets.RpSettingsGroup
import me.kafuuneko.rpclient.ui.widgets.RpSettingsTile

/**
 * 实验性模块面板。
 *
 * 为什么单独一栏而不是散进各个功能所在的组：这里的开关都会改变
 * 「角色怎么理解这个世界」，出错的方式不是界面错位而是情节失控。
 * 集中放在一起，用户才能一眼看清自己开了哪些、以及随时全关回去。
 *
 * 列表由 [ExperimentalFeature] 枚举驱动，加一个功能不用改这个文件。
 */
@Composable
internal fun ExperimentalFeaturesPanel(
    state: MainExperimentalState,
    emit: MainUiIntent.() -> Unit
) {
    RpSettingsGroup {
        ExperimentalHeader(highRiskCount = state.highRiskCount)
        ExperimentalFeature.entries.forEach { feature ->
            ExperimentalRow(
                feature = feature,
                checked = feature.key in state.enabledKeys,
                onToggle = { MainUiIntent.ToggleExperimentalFeature(feature.key).emit() }
            )
        }
    }
}

@Composable
private fun ExperimentalHeader(highRiskCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Rounded.Science,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.experimental_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = stringResource(R.string.experimental_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (highRiskCount > 0) {
            Text(
                text = stringResource(R.string.experimental_high_risk_active, highRiskCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ExperimentalRow(
    feature: ExperimentalFeature,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(feature.titleRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (feature.available) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                RiskBadge(feature.risk)
                if (!feature.available) {
                    Text(
                        text = stringResource(R.string.experimental_not_available),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = stringResource(feature.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            enabled = feature.available
        )
    }
}

/** 风险徽标。颜色只区分三档，不做渐变——这是安全提示，不是装饰。 */
@Composable
private fun RiskBadge(risk: ExperimentalRisk) {
    val (labelRes, color) = when (risk) {
        ExperimentalRisk.Low ->
            R.string.experimental_risk_low to MaterialTheme.colorScheme.primary
        ExperimentalRisk.Medium ->
            R.string.experimental_risk_medium to MaterialTheme.colorScheme.tertiary
        ExperimentalRisk.High ->
            R.string.experimental_risk_high to MaterialTheme.colorScheme.error
    }
    Text(
        text = stringResource(labelRes),
        style = MaterialTheme.typography.labelSmall,
        color = color
    )
}
