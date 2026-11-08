package app.bilipai.feature.video.share

import androidx.compose.runtime.Composable
import app.bilipai.feature.share.ContentDynamicShareSheet

@Composable
internal fun VideoShareToDynamicDialog(payload: VideoSharePayload, onDismiss: () -> Unit) {
    ContentDynamicShareSheet(
        payload.asContentSharePayload(),
        onDismiss,
        presentationOverride = videoSharePresentation(isLandscapeVideoShare()),
    ) { VideoShareFeedbackEvents.prepared(payload.bvid) }
}
