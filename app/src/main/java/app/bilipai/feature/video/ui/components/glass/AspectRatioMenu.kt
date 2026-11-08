package app.bilipai.feature.video.ui.components.glass

import app.bilipai.feature.video.ui.components.*
import app.bilipai.core.ui.AppContentColors
import app.bilipai.core.ui.components.AppIcon
import app.bilipai.core.ui.components.AppText
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.bilipai.core.ui.components.AppSurface
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.ContainerLevel

@Composable
fun AspectRatioMenu(
    currentRatio: VideoAspectRatio,
    onRatioSelected: (VideoAspectRatio) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) = app.bilipai.feature.video.ui.components.AspectRatioMenu(
    currentRatio = currentRatio,
    onRatioSelected = onRatioSelected,
    onDismiss = onDismiss,
    modifier = modifier,
)

/**
 * 比例按钮（用于底部控制栏）
 */
@Composable
fun AspectRatioButton(
    currentRatio: VideoAspectRatio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppSurface(
        onClick = onClick,
        modifier = modifier,
        shape = AppShapes.container(ContainerLevel.Chip),
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            AppIcon(
                Icons.Outlined.Star,
                contentDescription = "画面比例",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            AppText(
                text = currentRatio.displayName,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
