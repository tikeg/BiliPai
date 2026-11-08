package app.bilipai.core.ui.renderer.miuix

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.bilipai.core.ui.components.appDesktopFocusableItemVisuals
import top.yukonga.miuix.kmp.basic.RadioButton
import top.yukonga.miuix.kmp.basic.RadioButtonDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme
import app.bilipai.core.theme.resolveControlGlyphColor

@Composable
internal fun AppMiuixRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier,
    enabled: Boolean,
) {
    val scheme = MiuixTheme.colorScheme
    val colors = if (app.bilipai.core.ui.isMiuixNonGlassEnabled()) {
        RadioButtonDefaults.radioButtonColors()
    } else RadioButtonDefaults.radioButtonColors(
        selectedColor = resolveControlGlyphColor(scheme.primary, scheme.surface, scheme.surface),
    )
    ProvideAppMiuixHapticFeedback(scene = app.bilipai.core.ui.AppHapticScene.MENUS) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = modifier.appDesktopFocusableItemVisuals(enabled && onClick != null),
            enabled = enabled,
            colors = colors,
        )
    }
}
