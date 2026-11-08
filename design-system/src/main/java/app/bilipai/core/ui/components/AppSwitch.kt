package app.bilipai.core.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.renderer.material3.AppMaterial3Switch
import app.bilipai.core.ui.renderer.miuix.AppMiuixSwitch

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    showThumbIcon: Boolean = true,
) {
    // Miuix and the glass switch retain their own toggle/drag feedback.
    val materialChange = if (onCheckedChange != null) {
        appValueChangeWithFeedback<Boolean>(
            scene = app.bilipai.core.ui.AppHapticScene.CONTROLS,
            feedbackType = { if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff },
            onValueChange = onCheckedChange,
        )
    } else null
    AppExactTouchTargets {
        if (useAppLiquidValueControls()) {
            AppLiquidSwitch(
                checked, onCheckedChange, modifier, enabled, thumbContent, interactionSource,
            )
        } else when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> AppMaterial3Switch(
            checked = checked,
            onCheckedChange = materialChange,
            modifier = modifier,
            thumbContent = thumbContent,
            enabled = enabled,
            interactionSource = interactionSource,
            showThumbIcon = showThumbIcon,
        )
        AppUiStyle.MIUIX -> AppMiuixSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
        )
    }
    }
}
