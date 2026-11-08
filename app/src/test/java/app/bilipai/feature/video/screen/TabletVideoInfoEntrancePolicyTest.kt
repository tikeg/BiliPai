package app.bilipai.feature.video.screen

import app.bilipai.core.ui.adaptive.MotionTier
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TabletVideoInfoEntrancePolicyTest {
    @Test
    fun enhancedMotion_usesSlowerTopDownStagger() {
        val normal = resolveTabletVideoInfoEntranceSpec(MotionTier.Normal, false)
        val enhanced = resolveTabletVideoInfoEntranceSpec(MotionTier.Enhanced, false)

        assertTrue(enhanced.enabled)
        assertTrue(enhanced.durationMillis > normal.durationMillis)
        assertTrue(enhanced.staggerDelayMillis > normal.staggerDelayMillis)
    }

    @Test
    fun foldReducedMotion_disablesStagger() {
        val spec = resolveTabletVideoInfoEntranceSpec(MotionTier.Reduced, false)

        assertFalse(spec.enabled)
    }

    @Test
    fun systemReduceMotion_overridesEnhancedTier() {
        val spec = resolveTabletVideoInfoEntranceSpec(MotionTier.Enhanced, true)

        assertFalse(spec.enabled)
    }
}
