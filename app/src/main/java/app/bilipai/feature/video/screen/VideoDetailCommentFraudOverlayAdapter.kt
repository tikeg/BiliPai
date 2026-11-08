package app.bilipai.feature.video.screen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.bilipai.feature.video.ui.components.CommentFraudFeedbackHost
import app.bilipai.feature.video.viewmodel.VideoCommentViewModel
import app.bilipai.feature.video.viewmodel.VideoPlaybackViewModel

@Composable
internal fun VideoDetailCommentFraudOverlayAdapter(
    context: Context,
    playbackViewModel: VideoPlaybackViewModel,
    commentViewModel: VideoCommentViewModel,
    aid: Long?,
    fraudDetectionEnabled: Boolean,
) {
    LaunchedEffect(playbackViewModel, commentViewModel, aid, fraudDetectionEnabled) {
        if (aid == null) return@LaunchedEffect
        playbackViewModel.commentSentEvent.collect { event ->
            commentViewModel.onExternalCommentSent(
                aid = event.aid,
                newReply = event.reply,
                fraudDetectionEnabled = fraudDetectionEnabled,
                senderAccount = event.account,
            )
        }
    }

    CommentFraudFeedbackHost(commentViewModel = commentViewModel)
}
