package app.bilipai.feature.video.ui.components

import android.content.Context
import app.bilipai.core.util.FormatUtils
import app.bilipai.data.model.response.ReplyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal data class ReplyCommentImageSpec(
    val authorName: String,
    val message: String,
    val metadataText: String,
    val qrUrl: String,
    val footerText: String,
    val generatedAtText: String
)

internal fun buildReplyCommentImageSpec(
    item: ReplyItem,
    generatedAtMillis: Long = System.currentTimeMillis()
): ReplyCommentImageSpec {
    val url = resolveReplyCommentShareUrl(item)
    val likeText = item.like.takeIf { it > 0 }?.let { "${it}赞" }
    // 保存的评论图片始终保留完整发布时间（含秒），不受“详细评论时间显示”偏好影响
    val metadata = listOfNotNull(

        FormatUtils.formatPrecisePublishTime(
            timestampSeconds = item.ctime,
            pattern = "yyyy-MM-dd HH:mm:ss"
        ).takeIf { it.isNotBlank() },
        likeText
    ).joinToString(" · ")
    val generatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        .format(Date(generatedAtMillis))
    return ReplyCommentImageSpec(
        authorName = item.member.uname.ifBlank { "未知用户" },
        message = item.content.message.trim().ifBlank { "（空评论）" },
        metadataText = metadata,
        qrUrl = url,
        footerText = "识别二维码，查看评论",
        generatedAtText = generatedAt
    )
}

suspend fun saveReplyCommentImageToGallery(context: Context, item: ReplyItem): Boolean = withContext(Dispatchers.IO) {
    try { app.bilipai.feature.share.saveShareImageDocument(context, app.bilipai.feature.share.replyShareDocument(item)) } catch (cancelled: kotlinx.coroutines.CancellationException) { throw cancelled } catch (_: Exception) { false }
}
