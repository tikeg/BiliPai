package app.bilipai.core.ui

import app.bilipai.core.theme.AppUiStyle
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppSquircleModifiersTest {

    @Test
    fun squircleBackground_appliesOnlyOnMiuixStyle() {
        assertTrue(shouldApplyMiuixSquircleBackground(AppUiStyle.MIUIX))
        assertFalse(shouldApplyMiuixSquircleBackground(AppUiStyle.MATERIAL3))
    }
}
