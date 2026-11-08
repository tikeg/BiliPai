package app.bilipai.feature.video.ui.overlay

import app.bilipai.core.store.PlaybackCompletionBehavior

/** Source order is independent of persisted enum values and application material. */
internal val playerPlaybackOrderOptions = listOf(
    PlaybackCompletionBehavior.STOP_AFTER_CURRENT,
    PlaybackCompletionBehavior.PLAY_IN_ORDER,
    PlaybackCompletionBehavior.REPEAT_ONE,
    PlaybackCompletionBehavior.LOOP_PLAYLIST,
    PlaybackCompletionBehavior.CONTINUE_CURRENT_LOGIC,
)
