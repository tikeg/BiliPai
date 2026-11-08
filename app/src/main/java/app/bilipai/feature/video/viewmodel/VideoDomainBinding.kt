package app.bilipai.feature.video.viewmodel

internal fun shouldRebindVideoDomain(
    current: VideoSubjectSnapshot?,
    next: VideoSubjectSnapshot
): Boolean = current?.generation != next.generation
