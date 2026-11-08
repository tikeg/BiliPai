package app.bilipai.core.ui.renderer.material3

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.MaterialTheme
import app.bilipai.core.theme.resolveControlGlyphColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.bilipai.core.ui.components.appDesktopInteractionVisuals

@Composable
internal fun AppMaterial3RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val nativeColors = RadioButtonDefaults.colors()
    val surface = MaterialTheme.colorScheme.surface
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier.appDesktopInteractionVisuals(
            resolvedInteractionSource,
            enabled && onClick != null,
        ),
        enabled = enabled,
        colors = nativeColors.copy(selectedColor = resolveControlGlyphColor(nativeColors.selectedColor, surface, surface)),
        interactionSource = resolvedInteractionSource,
    )
}
