package app.bilipai.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import app.bilipai.core.ui.AppActionHaptics
import app.bilipai.core.ui.LocalAppThemeConfig

/** 已有场景反馈优先；操作未提供反馈时补一次轻触，嵌套组件共用操作边界。 */
@Composable
fun appActionWithFeedback(onClick: () -> Unit): () -> Unit =
    appActionWithFeedback(app.bilipai.core.ui.AppHapticScene.CONTROLS, onClick)

@Composable
fun appActionWithFeedback(
    scene: app.bilipai.core.ui.AppHapticScene,
    onClick: () -> Unit,
): () -> Unit {
    val haptic = app.bilipai.core.ui.rememberAppHapticFeedback(scene)
    val enabled = LocalAppThemeConfig.current.hapticFeedbackEnabled
    return {
        AppActionHaptics.runAction {
            onClick()
            if (enabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }
}

/** 选择型弹层共用点击去重边界，值的校验与保存仍由调用方负责。 */
@Composable
fun <T> appValueChangeWithFeedback(
    feedbackType: (T) -> HapticFeedbackType = { HapticFeedbackType.TextHandleMove },
    scene: app.bilipai.core.ui.AppHapticScene = app.bilipai.core.ui.AppHapticScene.MENUS,
    onValueChange: (T) -> Unit,
): (T) -> Unit {
    val haptic = app.bilipai.core.ui.rememberAppHapticFeedback(scene)
    val enabled = LocalAppThemeConfig.current.hapticFeedbackEnabled
    return { value ->
        AppActionHaptics.runAction {
            onValueChange(value)
            if (enabled) haptic.performHapticFeedback(feedbackType(value))
        }
    }
}
