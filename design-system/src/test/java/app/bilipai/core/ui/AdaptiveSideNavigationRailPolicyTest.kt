package app.bilipai.core.ui

import app.bilipai.core.theme.AppUiStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdaptiveSideNavigationRailPolicyTest {

    @Test
    fun miuixStyleUsesMiuixRailRenderer() {
        assertEquals(
            AdaptiveSideNavigationRailRenderer.MIUIX,
            resolveAdaptiveSideNavigationRailRenderer(AppUiStyle.MIUIX),
        )
    }

    @Test
    fun material3StyleKeepsMaterial3RailRenderer() {
        assertEquals(
            AdaptiveSideNavigationRailRenderer.MATERIAL3,
            resolveAdaptiveSideNavigationRailRenderer(AppUiStyle.MATERIAL3),
        )
    }

    @Test
    fun expandableOnlyWhenExpandedWidthClass() {
        assertTrue(shouldUseExpandableMiuixNavigationRail(isExpandedWidthClass = true))
        assertFalse(shouldUseExpandableMiuixNavigationRail(isExpandedWidthClass = false))
    }
}
