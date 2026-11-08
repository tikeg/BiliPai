package app.bilipai.feature.live.components

import androidx.compose.runtime.Composable
import app.bilipai.feature.share.ContentMessageShareSheet
import app.bilipai.feature.share.buildLiveSharePayload

@Composable
internal fun LiveShareToMessageSheet(
    roomId: Long,
    roomTitle: String,
    onDismiss: () -> Unit,
    onResult: (Boolean, String) -> Unit,
    cover: String = "",
    author: String = "",
    authorId: Long = 0,
) {
    ContentMessageShareSheet(buildLiveSharePayload(roomId, roomTitle, cover, author, authorId), onDismiss) {
        onResult(true, "已分享给 $it 人")
    }
}
