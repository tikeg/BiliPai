package app.bilipai.feature.dynamic.components

import app.bilipai.core.util.HapticType

internal fun resolveImagePreviewLongPressSaveStartFeedback(): HapticType {
    return HapticType.MEDIUM
}

internal fun resolveImagePreviewSaveFeedback(success: Boolean): HapticType {
    return if (success) HapticType.LIGHT else HapticType.HEAVY
}
