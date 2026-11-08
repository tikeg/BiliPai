package app.bilipai.feature.video.ui.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import app.bilipai.core.ui.AppContentColors
import app.bilipai.core.util.FormatUtils
import app.bilipai.feature.video.ui.overlay.BiliTvAnimatedBufferingIndicator

// 将封面宽高比计算和加载提示的分支放在独立组合边界，缩小主播放函数的寄存器活跃范围。
@Composable
internal fun PortraitVideoCoverLayer(
    cover: String,
    currentVideoAspect: Float,
    useViewportBoundCover: Boolean,
    hasKnownVideoAspect: Boolean,
    showBuffering: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(Color.Black)) {
        if (useViewportBoundCover) {
            PortraitVideoViewportContainer(
                currentVideoAspect = resolvePortraitCoverViewportAspect(
                    currentVideoAspect = currentVideoAspect,
                    hasRenderedFirstFrame = hasKnownVideoAspect,
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
                AsyncImage(
                    model = FormatUtils.fixImageUrl(cover),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = resolvePortraitCoverContentScale(isVerticalVideo = currentVideoAspect < 1f),
                )
            }
        } else {
            AsyncImage(
                model = FormatUtils.fixImageUrl(cover),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = resolvePortraitCoverContentScale(isVerticalVideo = currentVideoAspect < 1f),
            )
        }

        if (showBuffering) {
            BiliTvAnimatedBufferingIndicator(
                modifier = Modifier.align(Alignment.Center),
                tint = AppContentColors.accent(),
            )
        }
    }
}
