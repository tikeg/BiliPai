package app.bilipai.core.ui.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.bilipai.core.ui.LocalAppThemeConfig
import app.bilipai.core.ui.adaptive.MotionTier
import app.bilipai.core.ui.adaptive.RuntimeVisualGuardDecision
import app.bilipai.core.ui.adaptive.resolveDeviceUiProfile
import app.bilipai.core.util.LocalWindowSizeClass
import app.bilipai.core.util.WindowWidthSizeClass

private val NormalGuardDecision = RuntimeVisualGuardDecision(
    effectiveMotionTier = MotionTier.Normal,
    forceLowBlurBudget = false,
    downgraded = false,
    nextLastDowngradeAtMs = null,
)

/**
 * 守卫决策的分发通道。
 *
 * 值刻意是 [State] 容器而不是 [RuntimeVisualGuardDecision] 本身：CompositionLocal 的值
 * 恒定不变，provider 侧永远不会因为降级/恢复而重组整棵树；只有真正读 `.value` 的
 * 叶子（`unifiedBlur`、液态玻璃）才会失效。
 */
internal val LocalRuntimeVisualGuard = staticCompositionLocalOf<State<RuntimeVisualGuardDecision>> {
    mutableStateOf(NormalGuardDecision)
}

/** Resolve structural effect fallbacks at the rendering leaf that actually owns the effect. */
@Composable
internal fun isLowBlurBudgetForced(explicitlyForced: Boolean = false): Boolean =
    explicitlyForced || LocalRuntimeVisualGuard.current.value.forceLowBlurBudget

@Composable
internal fun ProvideRuntimeVisualGuard(
    widthSizeClass: WindowWidthSizeClass = LocalWindowSizeClass.current.widthSizeClass,
    guardEnabled: Boolean = LocalAppThemeConfig.current.runtimeVisualGuardEnabled,
    content: @Composable () -> Unit,
) {
    // 设备基线档位只能在拿到 window metrics 之后才知道，而 Tracker 是进程单例、
    // 在 Activity 之前就已加载。放在组合根注入还能让折叠屏展开/分屏自动跟随。
    LaunchedEffect(widthSizeClass, guardEnabled) {
        AppRuntimeVisualGuardTracker.setBaseTier(
            resolveDeviceUiProfile(widthSizeClass).motionTier
        )
        AppRuntimeVisualGuardTracker.setEnabled(guardEnabled)
    }

    val decision = AppRuntimeVisualGuardTracker.decision.collectAsStateWithLifecycle()
    CompositionLocalProvider(LocalRuntimeVisualGuard provides decision, content = content)
}
