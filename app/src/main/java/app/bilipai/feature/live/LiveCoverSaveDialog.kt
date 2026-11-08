package app.bilipai.feature.live

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import app.bilipai.core.ui.AppAlertDialog
import app.bilipai.core.ui.components.AppText
import app.bilipai.core.ui.components.AppTextButton
import app.bilipai.feature.download.DownloadManager
import kotlinx.coroutines.launch

@Composable
internal fun LiveCoverSaveDialog(card: LiveRoomCardUiModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    AppAlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("保存直播封面") },
        text = { AppText(card.title) },
        confirmButton = {
            AppTextButton(onClick = {
                scope.launch {
                    val saved = DownloadManager.saveImageToGallery(context, card.coverUrl, card.title)
                    Toast.makeText(context, if (saved) "封面已保存到相册" else "封面保存失败，请重试", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            }) { AppText("保存") }
        },
        dismissButton = { AppTextButton(onClick = onDismiss) { AppText("取消") } },
    )
}
