package app.bilipai.feature.settings

import androidx.compose.ui.unit.dp
import app.bilipai.core.ui.AppAdaptiveSceneLayout
import app.bilipai.core.ui.AppSplitLayoutState
import app.bilipai.core.ui.AppSplitPane
import app.bilipai.core.ui.resolveAppAdaptiveSceneLayout
import app.bilipai.core.util.AppWindowAdaptiveInfo
import app.bilipai.core.util.WindowHeightSizeClass
import app.bilipai.core.util.WindowSizeClass
import app.bilipai.core.util.WindowWidthSizeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsTabletShellPolicyTest {
    @Test
    fun mediumAndExpandedWindowsResolveExpectedPaneCounts() {
        assertEquals(
            AppAdaptiveSceneLayout.OptionalTwoPane,
            resolveAppAdaptiveSceneLayout(adaptiveInfo(WindowWidthSizeClass.Medium)),
        )
        assertEquals(
            AppAdaptiveSceneLayout.TwoPane,
            resolveAppAdaptiveSceneLayout(adaptiveInfo(WindowWidthSizeClass.Expanded)),
        )
        assertEquals(
            AppAdaptiveSceneLayout.ThreePane,
            resolveAppAdaptiveSceneLayout(adaptiveInfo(WindowWidthSizeClass.ExtraLarge)),
        )
    }

    @Test
    fun paneNavigatorKeepsDetailSelectionAndPredictableBackHistory() {
        val state = AppSplitLayoutState(listOf(AppSplitPane.Primary))

        state.navigateTo(AppSplitPane.Secondary)
        assertEquals(AppSplitPane.Secondary, state.currentPane)
        assertTrue(state.navigateBack())
        assertEquals(AppSplitPane.Primary, state.currentPane)
    }

    private fun adaptiveInfo(widthClass: WindowWidthSizeClass) = AppWindowAdaptiveInfo(
        windowSizeClass = WindowSizeClass(
            widthSizeClass = widthClass,
            heightSizeClass = WindowHeightSizeClass.Medium,
            widthDp = 1000.dp,
            heightDp = 700.dp,
        ),
    )

    @Test
    fun shouldRenderSettingsTabletDetailPane_returnsTrueForCategoryOrSearch() {
        // Root settings with no category selected -> false (empty detail pane)
        kotlin.test.assertFalse(
            shouldRenderSettingsTabletDetailPane(selectedCategory = null, isSearchActive = false)
        )
        // Category selected -> true
        assertTrue(
            shouldRenderSettingsTabletDetailPane(
                selectedCategory = SettingsRootCategory.APPEARANCE_THEME,
                isSearchActive = false
            )
        )
        // Search active -> true (even with no category selected)
        assertTrue(
            shouldRenderSettingsTabletDetailPane(selectedCategory = null, isSearchActive = true)
        )
    }

    @Test
    fun isSettingsSearchNavKey_matchesSettingsSearch() {
        assertTrue(isSettingsSearchNavKey(app.bilipai.navigation3.BiliPaiNavKey.SettingsSearch))
        kotlin.test.assertFalse(isSettingsSearchNavKey(app.bilipai.navigation3.BiliPaiNavKey.Settings))
        kotlin.test.assertFalse(
            isSettingsSearchNavKey(app.bilipai.navigation3.BiliPaiNavKey.AppearanceSettings)
        )
    }
}
