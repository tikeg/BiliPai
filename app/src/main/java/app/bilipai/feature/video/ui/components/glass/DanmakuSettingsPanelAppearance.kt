package app.bilipai.feature.video.ui.components.glass

import app.bilipai.feature.video.ui.components.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.bilipai.core.theme.resolveAccessibleContainerColors
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.ContainerLevel
import app.bilipai.core.ui.LocalAppThemeConfig
import app.bilipai.core.ui.components.AppOutlinedButton
import app.bilipai.core.ui.liquidControlThemeColor

@Composable
internal fun rememberDanmakuPanelColors(scheme: ColorScheme): DanmakuSettingsPanelSurfaceColors {
    val glass = LocalAppThemeConfig.current.liquidGlassEnabled
    val accent = liquidControlThemeColor()
    return remember(scheme, glass, accent) {
        val native = resolveDanmakuSettingsPanelSurfaceColors(scheme)
        // Glass controls use the exact custom accent, not generated Material palette tones.
        // Keeping the original palette intact preserves both native theme branches.
        if (!glass) native else {
            val badge = resolveAccessibleContainerColors(
                containerColor = accent.copy(alpha = 0.16f),
                contentColor = accent,
                backgroundColor = native.itemColor,
                fallbackContentColors = listOf(scheme.onSurface, Color.White, Color.Black),
            )
            native.copy(
                badgeBackgroundColor = badge.containerColor,
                badgeContentColor = badge.contentColor,
                badgeBorderColor = accent.copy(alpha = 0.4f),
                sliderActiveTrackColor = accent,
                sliderActiveTickColor = accent.copy(alpha = 0.4f),
                sliderThumbColor = accent,
                resetButtonColor = accent,
                resetButtonBackgroundColor = accent.copy(alpha = 0.12f),
            )
        }
    }
}

@Composable
internal fun DanmakuSyncButton(
    onClick: () -> Unit,
    enabled: Boolean,
    contentPadding: PaddingValues,
    accent: Color,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    if (LocalAppThemeConfig.current.liquidGlassEnabled) {
        AppOutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.size(width = 100.dp, height = 36.dp),
            shape = AppShapes.container(ContainerLevel.Chip),
            contentPadding = contentPadding,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = accent),
            border = BorderStroke(1.dp, accent.copy(alpha = if (enabled) 0.5f else 0.2f)),
            content = content,
        )
    } else {
        AppOutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            contentPadding = contentPadding,
            shape = AppShapes.container(ContainerLevel.Field),
            content = content,
        )
    }
}
