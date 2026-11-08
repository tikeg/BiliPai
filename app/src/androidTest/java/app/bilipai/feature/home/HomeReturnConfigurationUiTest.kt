package app.bilipai.feature.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import app.bilipai.core.store.AppNavigationSettings
import app.bilipai.core.store.HomeSettings
import app.bilipai.feature.settings.LocalSettingsHomeConfiguration
import app.bilipai.feature.settings.LocalSettingsNavigationConfiguration
import app.bilipai.feature.settings.rememberSettingsHomeConfiguration
import app.bilipai.feature.settings.rememberSettingsNavigationConfiguration
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeReturnConfigurationUiTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun restoredContentStartsWithHostSettingsAndTracksMaterialChanges() {
        val home = mutableStateOf(HomeSettings(
            displayMode = 1, androidNativeLiquidGlassEnabled = true, topTabLabelMode = 0,
        ))
        val navigation = mutableStateOf(AppNavigationSettings(tabletUseSidebar = true))
        val frames = mutableListOf<Pair<HomeSettings, AppNavigationSettings>>()
        rule.setContent {
            CompositionLocalProvider(
                LocalSettingsHomeConfiguration provides home.value,
                LocalSettingsNavigationConfiguration provides navigation.value,
            ) {
                val resolvedHome = rememberSettingsHomeConfiguration()
                val resolvedNavigation = rememberSettingsNavigationConfiguration()
                SideEffect { frames.add(resolvedHome to resolvedNavigation) }
            }
        }
        rule.waitForIdle()
        rule.runOnIdle {
            assertTrue(frames.isNotEmpty())
            assertTrue(frames.all { it.first === home.value && it.second === navigation.value })
            home.value = home.value.copy(androidNativeLiquidGlassEnabled = false, displayMode = 0)
            navigation.value = navigation.value.copy(tabletUseSidebar = false)
        }
        rule.waitForIdle()
        rule.runOnIdle {
            assertSame(home.value, frames.last().first)
            assertSame(navigation.value, frames.last().second)
        }
    }
}
