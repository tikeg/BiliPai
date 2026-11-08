package app.bilipai.core.ui.components

import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.ui.AppIconStyle
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppPreferenceIconVisualsTest {
    @Test
    fun `explicit neutral styles cannot be overridden by the settings color provider`() {
        for (theme in AppUiStyle.entries) {
            for (style in listOf(AppIconStyle.THEME_CONTAINER, AppIconStyle.MD3_STANDARD)) {
                assertFalse(resolveUsesColorfulPreferenceIcons(style, theme, settingsScope = true))
            }
        }
    }

    @Test
    fun `explicit colorful choices apply globally while auto preserves native md3`() {
        for (theme in AppUiStyle.entries) {
            for (style in listOf(AppIconStyle.SOFT_COLORFUL, AppIconStyle.VIVID_COLORFUL)) {
                assertTrue(resolveUsesColorfulPreferenceIcons(style, theme, settingsScope = false))
            }
        }
        assertFalse(resolveUsesColorfulPreferenceIcons(AppIconStyle.AUTO, AppUiStyle.MATERIAL3, true))
        assertFalse(resolveUsesColorfulPreferenceIcons(AppIconStyle.AUTO, AppUiStyle.MIUIX, false))
        assertTrue(resolveUsesColorfulPreferenceIcons(AppIconStyle.AUTO, AppUiStyle.MIUIX, true))
    }
}
