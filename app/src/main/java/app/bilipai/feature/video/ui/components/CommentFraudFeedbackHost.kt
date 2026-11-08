package app.bilipai.feature.video.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import app.bilipai.data.model.CommentFraudStatus
import app.bilipai.data.repository.resolveCommentFraudLightMessage
import app.bilipai.data.repository.shouldShowCommentFraudResultDialog
import app.bilipai.feature.video.viewmodel.VideoCommentViewModel

/** Shared feedback for ordinary videos, episode comments and comment sheets. */
@Composable
internal fun CommentFraudFeedbackHost(
    commentViewModel: VideoCommentViewModel,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    var dialogStatus by remember(commentViewModel, enabled) { mutableStateOf<CommentFraudStatus?>(null) }
    LaunchedEffect(commentViewModel, enabled, context.applicationContext) {
        if (!enabled) return@LaunchedEffect
        commentViewModel.fraudEvent.collect { status ->
            val lightMessage = resolveCommentFraudLightMessage(status)
            if (lightMessage != null) {
                Toast.makeText(context, lightMessage, Toast.LENGTH_SHORT).show()
            } else if (shouldShowCommentFraudResultDialog(status)) {
                dialogStatus = status
            }
        }
    }
    dialogStatus?.let { status ->
        CommentFraudResultDialog(
            status = status,
            onDismiss = {
                dialogStatus = null
                commentViewModel.dismissFraudResult()
            },
            onDeleteComment = if (status == CommentFraudStatus.SHADOW_BANNED) {
                {
                    val rpid = commentViewModel.commentState.value.fraudDetectRpid
                    if (rpid > 0L) commentViewModel.startDissolve(rpid)
                }
            } else null,
        )
    }
}
