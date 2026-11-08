package app.bilipai.core.ui.renderer.material3

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import app.bilipai.core.theme.resolveControlGlyphColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.bilipai.core.ui.components.AppCheckboxColors
import app.bilipai.core.ui.components.appDesktopInteractionVisuals

@Composable
internal fun AppMaterial3Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier,
    enabled: Boolean,
    colors: AppCheckboxColors?,
    interactionSource: MutableInteractionSource?,
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val nativeColors = colors?.let {
        CheckboxDefaults.colors(
            checkedColor = it.checkedColor,
            uncheckedColor = it.uncheckedColor,
        )
    } ?: CheckboxDefaults.colors()
    // Overriding just the box must not leave its old checkmark on an unrelated fill.
    // Repair only the enabled checked glyph; disabled and unchecked native colors stay intact.
    val readableColors = nativeColors.copy(
        checkedCheckmarkColor = resolveControlGlyphColor(
            nativeColors.checkedCheckmarkColor, nativeColors.checkedBoxColor, MaterialTheme.colorScheme.surface,
        ),
    )
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.appDesktopInteractionVisuals(
            resolvedInteractionSource,
            enabled && onCheckedChange != null,
        ),
        enabled = enabled,
        colors = readableColors,
        interactionSource = resolvedInteractionSource,
    )
}
