package app.bilipai.core.ui

import app.bilipai.core.theme.AppUiStyle

enum class AdaptiveTooltipRenderer {
    MIUIX_TOOLTIP_BOX,
    MATERIAL3_TOOLTIP_BOX,
    PASSTHROUGH
}

fun resolveAdaptiveTooltipRenderer(
    uiStyle: AppUiStyle
): AdaptiveTooltipRenderer = when (uiStyle) {
    AppUiStyle.MIUIX -> AdaptiveTooltipRenderer.MIUIX_TOOLTIP_BOX
    AppUiStyle.MATERIAL3 -> AdaptiveTooltipRenderer.MATERIAL3_TOOLTIP_BOX
}
