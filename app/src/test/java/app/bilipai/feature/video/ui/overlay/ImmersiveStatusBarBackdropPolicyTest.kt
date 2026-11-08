package app.bilipai.feature.video.ui.overlay

import app.bilipai.feature.video.ambient.ambientIntervalMs
import app.bilipai.feature.video.ambient.ambientSampleSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImmersiveStatusBarBackdropPolicyTest {


    @Test
    fun `ambient capture balances visual freshness with low cpu and battery overhead`() {
        assertTrue(ambientIntervalMs(glow = false, saving = false, static = false) <= 125L)
        assertEquals(96 to 54, ambientSampleSize(1920, 1080))
        assertEquals(54 to 96, ambientSampleSize(1080, 1920))
    }

}
