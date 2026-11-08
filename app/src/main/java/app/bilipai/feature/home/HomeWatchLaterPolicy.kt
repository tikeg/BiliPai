package app.bilipai.feature.home

import app.bilipai.data.model.response.VideoItem

internal fun resolveWatchLaterAid(video: VideoItem): Long {
    return video.aid.takeIf { it > 0 } ?: video.id
}

