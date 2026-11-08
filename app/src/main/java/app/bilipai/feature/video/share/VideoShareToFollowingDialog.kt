package app.bilipai.feature.video.share

import androidx.compose.runtime.Composable
import app.bilipai.feature.share.ContentMessageShareSheet

@Composable
internal fun VideoShareToFollowingDialog(payload: VideoSharePayload, onDismiss: () -> Unit, onSuccess: (Int) -> Unit) {
    ContentMessageShareSheet(
        payload.asContentSharePayload(),
        onDismiss,
        presentationOverride = videoSharePresentation(isLandscapeVideoShare()),
        onSuccess = onSuccess,
    )
}
