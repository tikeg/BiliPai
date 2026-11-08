package app.bilipai.feature.list

import app.bilipai.data.model.resolveVideoDisplayProgressState as resolveSharedVideoDisplayProgressState

// Keep the mobile source API while both clients consume the original display policy.
typealias VideoProgressDisplayState = app.bilipai.data.model.VideoProgressDisplayState

internal fun resolveVideoDisplayProgressState(
    serverProgressSec: Int,
    durationSec: Int,
    localPositionMs: Long = 0L,
    viewAt: Long = 0L
): VideoProgressDisplayState = resolveSharedVideoDisplayProgressState(
    serverProgressSec = serverProgressSec,
    durationSec = durationSec,
    localPositionMs = localPositionMs,
    viewAt = viewAt
)
