package app.bilipai.feature.settings

import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.ui.components.AppSegmentOption

internal fun resolveThemeSelectionOptions(
    material3Label: String,
    miuixLabel: String,
): List<AppSegmentOption<AppUiStyle>> {
    return listOf(
        AppSegmentOption(AppUiStyle.MATERIAL3, material3Label),
        AppSegmentOption(AppUiStyle.MIUIX, miuixLabel),
    )
}
