package app.bilipai.core.ui.adaptive

fun resolveEffectiveMotionTier(
    baseTier: MotionTier,
    animationEnabled: Boolean
): MotionTier {
    if (!animationEnabled) return MotionTier.Reduced
    return baseTier
}
