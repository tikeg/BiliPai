package app.bilipai.feature.home.components

import app.bilipai.core.util.HapticType
import kotlin.test.Test
import kotlin.test.assertEquals

class TopBarInteractionPolicyTest {

    @Test
    fun homeTopBarTap_triggersLightHapticBeforeAction() {
        val events = mutableListOf<String>()

        performHomeTopBarTap(
            haptic = { type ->
                events += "haptic:${type.name}"
            },
            onClick = {
                events += "action"
            }
        )

        assertEquals(listOf("haptic:${HapticType.LIGHT.name}", "action"), events)
    }
}
