package app.bilipai.feature.home.components

import app.bilipai.core.util.HapticType
import kotlin.test.Test
import kotlin.test.assertEquals

class SideBarInteractionPolicyTest {

    @Test
    fun homeSideBarItemTap_triggersLightHapticBeforeNavigation() {
        val events = mutableListOf<String>()

        performHomeSideBarItemTap(
            haptic = { type ->
                events += "haptic:${type.name}"
            },
            onClick = {
                events += "navigate"
            }
        )

        assertEquals(listOf("haptic:${HapticType.LIGHT.name}", "navigate"), events)
    }
}
