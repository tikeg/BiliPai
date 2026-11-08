package app.bilipai.core.player.dash

import app.bilipai.data.model.response.DashAudio
import app.bilipai.data.model.response.DashVideo
import app.bilipai.core.player.policy.PlaybackQualityMode

data class AdaptiveDashPlaybackSource(
    val manifest: String,
    val videoTracks: List<DashVideo>,
    val audioTracks: List<DashAudio>,
    val playbackQualityMode: PlaybackQualityMode
)
