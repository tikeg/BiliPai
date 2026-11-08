package app.bilipai.core.ui.renderer.miuix

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import app.bilipai.core.ui.LocalAppThemeConfig

private object NoOpHapticFeedback : HapticFeedback {
    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) = Unit
}

@Composable
internal fun ProvideAppMiuixHapticFeedback(
    scene: app.bilipai.core.ui.AppHapticScene = app.bilipai.core.ui.AppHapticScene.CONTROLS,
    content: @Composable () -> Unit,
) {
    val platformHaptic = app.bilipai.core.ui.rememberAppHapticFeedback(scene)
    val effectiveHaptic = if (LocalAppThemeConfig.current.hapticFeedbackEnabled) {
        platformHaptic
    } else {
        NoOpHapticFeedback
    }
    CompositionLocalProvider(LocalHapticFeedback provides effectiveHaptic, content = content)
}
