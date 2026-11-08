// 文件路径: feature/dynamic/components/LiveCard.kt
package app.bilipai.feature.dynamic.components

import app.bilipai.core.ui.AppContentColors

import coil3.network.NetworkHeaders
import coil3.network.httpHeaders

import coil3.request.crossfade
import app.bilipai.core.ui.components.AppIcon
import app.bilipai.core.ui.components.AppText

import app.bilipai.core.ui.AppSpacingTokens

import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.ContainerLevel
import app.bilipai.core.ui.rememberAppPlayIcon
import app.bilipai.core.ui.components.AppCard
import app.bilipai.core.ui.components.AppCardDefaults
import app.bilipai.core.ui.components.AppCardShape

import android.util.Log
import androidx.compose.foundation.background
import app.bilipai.core.ui.components.appActionClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
//  Material Icons
import androidx.compose.material3.*
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.feature.live.rememberLiveCardContainerTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.bilipai.core.ui.components.PageAwareAsyncImage
//  已改用 AppContentColors.accent()
import app.bilipai.data.model.response.LiveMajor
import app.bilipai.data.model.response.LiveRcmdMajor
import app.bilipai.feature.dynamic.model.LiveContentInfo
import kotlinx.serialization.json.Json

/**
 *  直播卡片
 */
@Composable
fun LiveCard(
    liveRcmd: LiveRcmdMajor,
    onLiveClick: (roomId: Long, title: String, uname: String) -> Unit = { _, _, _ -> }
) {
    // 解析直播内容 JSON
    val liveInfo = remember(liveRcmd.content) {
        try {
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<LiveContentInfo>(liveRcmd.content)
        } catch (e: Exception) {
            //  添加日志帮助调试
            Log.e("LiveCard", "Failed to parse live_rcmd content: ${e.message}")
            Log.d("LiveCard", "Raw content: ${liveRcmd.content.take(500)}")
            null
        }
    }

    val context = LocalContext.current

    if (liveInfo != null) {
        val roomId = liveInfo.live_play_info?.room_id ?: 0L
        val title = liveInfo.live_play_info?.title ?: ""

        val transition = rememberLiveCardContainerTransition(
            roomId = roomId, title = title, hostName = "",
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            cornerRadius = AppShapes.resolveContainerCornerDp(ContainerLevel.Card, LocalAppUiStyle.current),
            onClick = { onLiveClick(roomId, title, "") },
        )
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .then(transition.modifier)
                .appActionClickable(onClick = transition.onClick),
            shape = AppCardShape.Semantic(ContainerLevel.Card),
            colors = AppCardDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppSpacingTokens.TripleExtraLarge + AppSpacingTokens.DoubleExtraLarge)
                    .padding(AppSpacingTokens.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 直播封面
                Box(
                    modifier = Modifier
                        .width(AppSpacingTokens.TripleExtraLarge * 2 + AppSpacingTokens.ExtraLarge)
                        .fillMaxHeight()
                        .clip(AppShapes.container(ContainerLevel.Chip))
                ) {
                    liveInfo.live_play_info?.cover?.let { coverUrl ->
                        val url = if (coverUrl.startsWith("http://")) coverUrl.replace("http://", "https://") else coverUrl
                        PageAwareAsyncImage(
                            model = coil3.request.ImageRequest.Builder(context)
                                .data(url)
                                .httpHeaders(NetworkHeaders.Builder().set("Referer", "https://www.bilibili.com/").build())
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // 直播标识
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(AppSpacingTokens.ExtraSmall)
                            .background(AppContentColors.accent(), AppShapes.container(ContainerLevel.Tag))
                            .padding(horizontal = AppSpacingTokens.ExtraSmall, vertical = AppSpacingTokens.Micro)
                    ) {
                        AppText("直播中", fontSize = MaterialTheme.typography.labelSmall.fontSize, color = AppContentColors.onAccent(), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(AppSpacingTokens.Small + AppSpacingTokens.Micro))

                // 直播信息
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    AppText(
                        liveInfo.live_play_info?.title ?: "直播中",
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = AppContentColors.primaryText()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppIcon(
                            rememberAppPlayIcon(),
                            null,
                            modifier = Modifier.size(AppSpacingTokens.Medium + AppSpacingTokens.Micro),
                            tint = AppContentColors.secondaryText().copy(0.6f)
                        )
                        AppText(
                            "${liveInfo.live_play_info?.online ?: 0} 人观看",
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = AppContentColors.secondaryText().copy(0.6f)
                        )
                    }
                }
            }
        }
    } else {
        // 无法解析时显示占位
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            shape = AppCardShape.Semantic(ContainerLevel.Card),
            colors = AppCardDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier.padding(AppSpacingTokens.Medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(AppSpacingTokens.DoubleExtraLarge + AppSpacingTokens.Small)
                        .background(AppContentColors.accent(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AppText("🔴", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
                Spacer(modifier = Modifier.width(AppSpacingTokens.Medium))
                AppText(
                    "直播中",
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = AppContentColors.primaryText()
                )
            }
        }
    }
}

@Composable
fun LiveMajorCard(
    live: LiveMajor,
    onLiveClick: (roomId: Long, title: String, uname: String) -> Unit = { _, _, _ -> }
) {
    val roomId = live.id.trim().toLongOrNull() ?: 0L
    if (roomId <= 0L) return
    val title = live.title.ifBlank { "直播间" }
    val context = LocalContext.current
    val transition = rememberLiveCardContainerTransition(
        roomId = roomId, title = title, hostName = "",
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        cornerRadius = AppShapes.resolveContainerCornerDp(ContainerLevel.Card, LocalAppUiStyle.current),
        onClick = { onLiveClick(roomId, title, "") },
    )
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(transition.modifier)
            .appActionClickable(onClick = transition.onClick),
        shape = AppCardShape.Semantic(ContainerLevel.Card),
        colors = AppCardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppSpacingTokens.TripleExtraLarge + AppSpacingTokens.DoubleExtraLarge)
                .padding(AppSpacingTokens.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(AppSpacingTokens.TripleExtraLarge * 2 + AppSpacingTokens.ExtraLarge)
                    .fillMaxHeight()
                    .clip(AppShapes.container(ContainerLevel.Chip))
            ) {
                if (live.cover.isNotBlank()) {
                    val url = if (live.cover.startsWith("http://")) {
                        live.cover.replace("http://", "https://")
                    } else {
                        live.cover
                    }
                    PageAwareAsyncImage(
                        model = coil3.request.ImageRequest.Builder(context)
                            .data(url)
                            .httpHeaders(NetworkHeaders.Builder().set("Referer", "https://www.bilibili.com/").build())
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(AppSpacingTokens.ExtraSmall)
                        .background(AppContentColors.accent(), AppShapes.container(ContainerLevel.Tag))
                        .padding(horizontal = AppSpacingTokens.ExtraSmall, vertical = AppSpacingTokens.Micro)
                ) {
                    AppText("直播中", fontSize = MaterialTheme.typography.labelSmall.fontSize, color = AppContentColors.onAccent(), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(AppSpacingTokens.Small + AppSpacingTokens.Micro))
            AppText(
                title,
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = AppContentColors.primaryText()
            )
        }
    }
}
