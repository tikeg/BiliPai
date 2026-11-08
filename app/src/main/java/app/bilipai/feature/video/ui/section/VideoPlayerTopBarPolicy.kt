package app.bilipai.feature.video.ui.section

internal fun resolveVideoPlayerOverlayHomeClick(
    onBack: () -> Unit,
    onHomeClick: (() -> Unit)?
): () -> Unit {
    return onHomeClick ?: onBack
}
