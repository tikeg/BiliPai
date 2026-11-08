package app.bilipai.feature.video.ui.components

import androidx.compose.runtime.Composable
import app.bilipai.core.ui.AppAlertDialog
import app.bilipai.core.ui.components.AppText
import app.bilipai.core.ui.components.AppTextButton

@Composable
internal fun DanmakuSameSendConfirmation(
    text: String,
    isSending: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AppAlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("发送同款弹幕？") },
        text = { AppText("将在当前播放位置发送：\n$text") },
        confirmButton = {
            AppTextButton(onClick = onConfirm, enabled = !isSending && text.isNotBlank()) {
                AppText(if (isSending) "发送中…" else "发送")
            }
        },
        dismissButton = { AppTextButton(onClick = onDismiss) { AppText("取消") } },
    )
}
