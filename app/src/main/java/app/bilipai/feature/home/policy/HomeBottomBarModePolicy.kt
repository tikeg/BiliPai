package app.bilipai.feature.home.policy

import app.bilipai.core.store.SettingsManager

internal fun resolveHomeBottomBarBaseVisibility(
    useSideNavigation: Boolean,
    mode: SettingsManager.BottomBarVisibilityMode
): Boolean? {
    if (useSideNavigation) return false
    return when (mode) {
        SettingsManager.BottomBarVisibilityMode.SCROLL_HIDE -> null
        SettingsManager.BottomBarVisibilityMode.ALWAYS_VISIBLE -> true
        SettingsManager.BottomBarVisibilityMode.ALWAYS_HIDDEN -> false
    }
}
