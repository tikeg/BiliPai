package app.bilipai.core.ui.components

import app.bilipai.core.ui.AppSpacingTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppContentStatePolicyTest {

    @Test
    fun pagePresentation_fillsAvailableSpaceWithSharedPadding() {
        val spec = resolveAppContentStateLayoutSpec(AppContentStatePresentation.PAGE)

        assertTrue(spec.fillsAvailableHeight)
        assertEquals(AppSpacingTokens.ExtraLarge, spec.horizontalPadding)
        assertEquals(AppSpacingTokens.DoubleExtraLarge, spec.verticalPadding)
    }

    @Test
    fun inlinePresentation_doesNotClaimListHeight() {
        val spec = resolveAppContentStateLayoutSpec(AppContentStatePresentation.INLINE)

        assertFalse(spec.fillsAvailableHeight)
        assertEquals(AppSpacingTokens.Large, spec.horizontalPadding)
        assertEquals(AppSpacingTokens.Large, spec.verticalPadding)
    }
}
