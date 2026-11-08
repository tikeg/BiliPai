package app.bilipai.feature.video.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.resolveCustomSelectionContentColor
import app.bilipai.core.ui.AppContentColors
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.isMiuixNonGlassEnabled

/** 自绘选项仅在 Miuix 非玻璃路径收口；MD3 与玻璃继续使用调用方原有样式。
 * 画面上的深色面板显式传入深色底面，避免跟随浅色应用主题生成浅底选中块。
 */
@Composable
internal fun playerOptionContainerColor(
    selected: Boolean,
    fallback: Color,
    surface: Color = AppSurfaceTokens.background(),
): Color = if (selected && isMiuixNonGlassEnabled()) {
    AppSurfaceTokens.resolveSelectionContainer(AppContentColors.accent(), surface, AppUiStyle.MIUIX, false)
} else fallback

@Composable
internal fun playerOptionContentColor(
    selected: Boolean,
    fallback: Color,
    surface: Color = AppSurfaceTokens.background(),
): Color = if (selected && isMiuixNonGlassEnabled()) {
    resolveCustomSelectionContentColor(
        AppContentColors.accent(), playerOptionContainerColor(true, fallback, surface),
    )
} else fallback

@Composable
internal fun playerOptionShape(fallback: Shape): Shape =
    if (isMiuixNonGlassEnabled()) AppShapes.SelectionIndicator else fallback
