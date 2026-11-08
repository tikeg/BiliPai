package app.bilipai.feature.plugin

import app.bilipai.plugin.sdk.Plugin
import kotlinx.coroutines.flow.StateFlow

/** Host-only settings controls, available even when recommendation execution is disabled. */
interface TodayWatchControls : Plugin {
    val configState: StateFlow<TodayWatchPluginConfig>
    fun setCurrentMode(mode: TodayWatchPluginMode)
    fun setCollapsed(collapsed: Boolean)
}
