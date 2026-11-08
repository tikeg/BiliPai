package app.bilipai.core.ui

import app.bilipai.core.theme.AppUiStyle
import kotlin.test.Test
import kotlin.test.assertEquals

class AdaptiveTooltipPolicyTest {

    @Test
    fun miuixUsesOfficialTooltipBoxRenderer() {
        assertEquals(
            AdaptiveTooltipRenderer.MIUIX_TOOLTIP_BOX,
            resolveAdaptiveTooltipRenderer(AppUiStyle.MIUIX)
        )
    }

    @Test
    fun material3UsesOfficialTooltipBoxRenderer() {
        assertEquals(
            AdaptiveTooltipRenderer.MATERIAL3_TOOLTIP_BOX,
            resolveAdaptiveTooltipRenderer(AppUiStyle.MATERIAL3)
        )
    }
}
