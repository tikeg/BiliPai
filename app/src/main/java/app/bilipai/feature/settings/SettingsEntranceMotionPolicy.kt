package app.bilipai.feature.settings

import app.bilipai.core.ui.adaptive.MotionTier
import app.bilipai.core.ui.adaptive.resolveEffectiveMotionTier

internal fun resolveAnimationSettingsCardMotionTier(
    baseTier: MotionTier,
    cardAnimationEnabled: Boolean
): MotionTier = resolveEffectiveMotionTier(
    baseTier = baseTier,
    animationEnabled = cardAnimationEnabled
)

internal fun shouldStartSettingsEntrance(
    entranceEnabled: Boolean,
    navigationTransitionRunning: Boolean,
): Boolean = entranceEnabled && !navigationTransitionRunning
