package app.bilipai.core.ui.adaptive

import androidx.compose.ui.unit.dp
import app.bilipai.core.util.AppWindowAdaptiveInfo
import app.bilipai.core.util.WindowHeightSizeClass
import app.bilipai.core.util.WindowSizeClass
import app.bilipai.core.util.WindowWidthSizeClass
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InputDevicePolicyTest {
    private val window = WindowSizeClass(
        widthSizeClass = WindowWidthSizeClass.Expanded,
        heightSizeClass = WindowHeightSizeClass.Medium,
        widthDp = 1000.dp,
        heightDp = 700.dp,
    )

    @Test
    fun precisePointerEnablesHoverWithoutRequiringKeyboard() {
        val policy = resolveInputDevicePolicy(
            AppWindowAdaptiveInfo(
                windowSizeClass = window,
                precisePointerConnected = true,
            ),
        )

        assertTrue(policy.enableHoverEffects)
        assertFalse(policy.enableKeyboardNavigation)
        assertTrue(policy.enablePointerFocus)
    }

    @Test
    fun hardwareKeyboardEnablesNavigationWithoutHover() {
        val policy = resolveInputDevicePolicy(
            AppWindowAdaptiveInfo(
                windowSizeClass = window,
                hardwareKeyboardConnected = true,
            ),
        )

        assertFalse(policy.enableHoverEffects)
        assertTrue(policy.enableKeyboardNavigation)
        assertTrue(policy.enablePointerFocus)
    }
}
