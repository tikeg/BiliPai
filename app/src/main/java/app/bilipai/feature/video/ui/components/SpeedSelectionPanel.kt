// 文件路径: feature/video/ui/components/SpeedSelectionPanel.kt
package app.bilipai.feature.video.ui.components

import app.bilipai.core.ui.AppContentColors
import app.bilipai.core.ui.components.AppText

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.bilipai.core.ui.components.AppSurface
import app.bilipai.core.ui.components.formatPlaybackSpeed
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.ContainerLevel

/**
 * 播放速度格式化工具
 */
object PlaybackSpeed {

    fun formatSpeed(speed: Float): String =
        if (speed == 1f) "倍速" else formatPlaybackSpeed(speed)

    fun formatSpeedFull(speed: Float): String =
        if (speed == 1f) "正常" else formatPlaybackSpeed(speed)
}

/**
 * 倍速按钮（用于底部控制栏）
 */
@Composable
fun SpeedButton(
    currentSpeed: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppSurface(
        onClick = onClick,
        modifier = modifier,
        shape = AppShapes.container(ContainerLevel.Chip),
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        AppText(
            text = PlaybackSpeed.formatSpeed(currentSpeed),
            color = if (currentSpeed != 1.0f) AppContentColors.accent() else Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (currentSpeed != 1.0f) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
