package app.bilipai.core.ui.renderer.miuix

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.bilipai.core.ui.components.AppSliderColors
import app.bilipai.core.ui.components.appDesktopFocusableItemVisuals
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SliderDefaults

@Composable
internal fun AppMiuixSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChangeFinished: (() -> Unit)?,
    colors: AppSliderColors?,
) {
    val nativeColors = colors?.let {
        SliderDefaults.sliderColors(
            foregroundColor = it.activeTrackColor,
            backgroundColor = it.inactiveTrackColor,
            thumbColor = it.thumbColor,
        )
    } ?: if (app.bilipai.core.ui.isMiuixNonGlassEnabled()) {
        SliderDefaults.sliderColors()
    } else SliderDefaults.sliderColors(thumbColor = app.bilipai.core.ui.components.appControlThumbColor())
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.appDesktopFocusableItemVisuals(enabled),
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = nativeColors,
    )
}
