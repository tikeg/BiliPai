package app.bilipai.feature.live

import app.bilipai.core.ui.AppContentColors

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.AppSpacingTokens
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.ContainerLevel
import app.bilipai.core.ui.components.AppSurface
import app.bilipai.core.ui.components.AppText

/**
 * BiliPai 风格的直播首页分区/标签 chip，使用中性 App* 组件与主题 Token 实现。
 */
@Composable
fun LiveHomeSelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    if (app.bilipai.core.ui.isMiuixNonGlassEnabled()) {
        app.bilipai.core.ui.components.AppFilterChip(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            miuixNavigationColors = true,
            label = { AppText(text = label) },
        )
        return
    }
    val container = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }
    val content = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        AppSurfaceTokens.onSurfaceVariantSummary()
    }
    AppSurface(
        onClick = onClick,
        modifier = modifier,
        color = container,
        contentColor = content,
        // 原生 MD3 与同类 tonal 标签共用胶囊圆角；其他材质保留自身几何。
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            if (app.bilipai.core.theme.LocalAppUiStyle.current ==
                app.bilipai.core.theme.AppUiStyle.MATERIAL3 &&
                !app.bilipai.core.ui.LocalAppThemeConfig.current.liquidGlassEnabled
            ) {
                app.bilipai.core.ui.renderer.material3.resolveTonalPillTabCornerRadius()
            } else 8.dp
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        AppText(
            text = label,
            color = content,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = if (compact) 13.sp else 14.sp, lineHeight = if (compact) 13.sp else 14.sp),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(
                horizontal = AppSpacingTokens.Small,
                vertical = 5.dp,
            ),
        )
    }
}
