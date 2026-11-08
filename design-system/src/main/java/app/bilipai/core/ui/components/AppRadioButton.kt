package app.bilipai.core.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.renderer.material3.AppMaterial3RadioButton
import app.bilipai.core.ui.renderer.miuix.AppMiuixRadioButton

@Composable
fun AppRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val materialClick = if (onClick != null) appActionWithFeedback(app.bilipai.core.ui.AppHapticScene.MENUS, onClick) else null
    when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> AppMaterial3RadioButton(
            selected = selected,
            onClick = materialClick,
            modifier = modifier,
            enabled = enabled,
            interactionSource = interactionSource,
        )
        AppUiStyle.MIUIX -> AppMiuixRadioButton(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
        )
    }
}
