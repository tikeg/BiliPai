package app.bilipai.feature.list

import androidx.compose.foundation.layout.Arrangement
import app.bilipai.core.ui.components.VideoStatusBadges
import app.bilipai.core.ui.AppContentColors

import coil3.request.crossfade

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import app.bilipai.core.ui.transition.videoCardSourceBoundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.videoCardTitleMaxLines
import app.bilipai.core.ui.videoCardTitleOverflow
import app.bilipai.core.ui.AppSpacingTokens
import app.bilipai.core.ui.ContainerLevel
import app.bilipai.core.ui.LocalAnimatedVisibilityScope
import app.bilipai.core.ui.LocalSharedTransitionScope
import app.bilipai.core.ui.components.AppIcon
import app.bilipai.core.ui.components.AppIconButton
import app.bilipai.core.ui.components.AppLinearProgressIndicator
import app.bilipai.core.ui.components.AppText
import app.bilipai.core.ui.components.UpBadgeName
import app.bilipai.core.ui.transition.LocalVideoCardSharedElementSourceRoute
import app.bilipai.core.ui.transition.LocalVideoSharedTransitionSpeedSettings
import app.bilipai.core.ui.transition.VideoCardSourceChromeSnapshot
import app.bilipai.core.ui.transition.VideoCardSourceCoverPresentation
import app.bilipai.core.ui.transition.VideoCardSourceLayout
import app.bilipai.core.ui.transition.rememberNativeVideoCardSnapshotController
import app.bilipai.core.ui.transition.resolveVideoCardSharedTransitionMotionSpec
import app.bilipai.core.ui.transition.shouldUseVideoCardShellSharedBounds
import app.bilipai.core.ui.transition.withMeasuredCoverDecodeSize
import app.bilipai.core.util.CardPositionManager
import app.bilipai.core.util.FormatUtils
import app.bilipai.data.model.response.VideoItem
import app.bilipai.feature.personal.PersonalCardSelectMask
import app.bilipai.feature.plugin.rememberSponsorFeedBadgeLabel
import app.bilipai.feature.personal.rememberPersonalCardVideoTransition
import app.bilipai.feature.personal.PersonalMediaCardFrame
import app.bilipai.feature.home.components.cards.HorizontalVideoStatRow
import app.bilipai.feature.home.components.cards.VideoCardCoverDurationText
import kotlin.math.roundToInt

internal fun resolveFavoriteDateLabel(
    timestampSeconds: Long,
    nowMs: Long = System.currentTimeMillis(),
): String =
    FormatUtils.formatPublishTime(timestampSeconds, nowMs).takeIf { it.isNotBlank() }
        ?.let { "${it}收藏" }
        .orEmpty()

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun FavoritePersonalCard(
    item: VideoItem,
    stacked: Boolean = false,
    transitionEnabled: Boolean,
    batchMode: Boolean,
    selected: Boolean,
    canRemove: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRemove: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val sponsorStatusLabel = rememberSponsorFeedBadgeLabel(item.bvid)
    val cardShape = AppShapes.container(ContainerLevel.Card)
    val transition = rememberPersonalCardVideoTransition(
        bvid = item.bvid,
        transitionEnabled = transitionEnabled,
        clipShape = cardShape,
    )
    val progressState = remember(item.progress, item.duration, item.view_at) {
        resolveVideoDisplayProgressState(
            serverProgressSec = item.progress,
            durationSec = item.duration,
            viewAt = item.view_at,
        )
    }
    val stationaryCoverUrl = remember(item.pic) { FormatUtils.fixImageUrl(item.pic) }
    val stationaryCoverRequest = remember(stationaryCoverUrl) {
        ImageRequest.Builder(context)
            .data(stationaryCoverUrl)
            .crossfade(false)
            .memoryCacheKey(stationaryCoverUrl)
            .diskCacheKey(stationaryCoverUrl)
            .build()
    }
    val cardCornerRadiusDp = AppShapes.containerCornerDp(ContainerLevel.Card).value.roundToInt()
    val triggerClick = {
        if (!batchMode) {
            transition.recordPosition(
                bvid = item.bvid,
                stacked = stacked,
                sourceCornerDp = cardCornerRadiusDp,
                chrome = VideoCardSourceChromeSnapshot(
                    title = item.title,
                    ownerName = item.owner.name.ifBlank { "未知UP主" },
                    ownerFaceUrl = item.owner.face,
                    viewText = FormatUtils.formatStat(item.stat.view.toLong()),
                    danmakuText = FormatUtils.formatStat(item.stat.danmaku.toLong()),
                    durationText = FormatUtils.formatDuration(item.duration),
                    infoPresentation = app.bilipai.core.ui.transition
                        .resolveVideoCardSourceInfoPresentation(
                            publishTimeText = resolveFavoriteDateLabel(item.view_at),
                            showStatsInInfo = true,
                            ownerBeforePublish = true,
                            showOverflowMenu = !batchMode && canRemove && onRemove != null,
                        ),
                    coverPresentation = VideoCardSourceCoverPresentation(
                        showDurationOnCover = true,
                        showHistoryProgressBar = progressState.showProgressBar,
                        historyProgressFraction = progressState.progressFraction,
                    ),
                    coverUrl = stationaryCoverUrl,
                    coverCacheKey = stationaryCoverUrl,
                )
            )
        }
        onClick()
    }

    PersonalMediaCardFrame(
        stacked = stacked,
        horizontalCoverMaxWidthDp = app.bilipai.feature.home.components.cards.WIDE_LIST_COVER_MAX_WIDTH_DP,
        coverModifier = Modifier.onGloballyPositioned {
            transition.bounds.coverBounds = it.videoCardSourceBoundsInRoot()
        },
        modifier = modifier
            .then(transition.shellModifier)
            .onGloballyPositioned { transition.bounds.cardBounds = it.videoCardSourceBoundsInRoot() },
        nativeSnapshotModifier = transition.nativeCardSnapshot.modifier,
        coverOverlayModifier = transition.nativeCardSnapshot.coverOverlayModifier,
        headlineContent = {
            AppText(
                text = item.title,
                style = app.bilipai.feature.home.components.cards.horizontalVideoCardTypography().title,
                maxLines = videoCardTitleMaxLines(),
                overflow = videoCardTitleOverflow(),
            )
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacingTokens.ExtraSmall)) {
                UpBadgeName(
                    name = item.owner.name.ifBlank { "未知UP主" },
                    nameStyle = app.bilipai.feature.home.components.cards.horizontalVideoCardTypography().author,
                    nameColor = AppContentColors.secondaryText(),
                    overflow = TextOverflow.Visible,
                )
                val dateLabel = resolveFavoriteDateLabel(item.view_at)
                if (dateLabel.isNotBlank()) {
                    AppText(
                        text = dateLabel,
                        style = app.bilipai.feature.home.components.cards.horizontalVideoCardTypography().statistic,
                        color = AppContentColors.secondaryText(),
                        overflow = TextOverflow.Visible,
                    )
                }
                HorizontalVideoStatRow(
                    playText = FormatUtils.formatStat(item.stat.view.toLong()),
                    danmakuText = FormatUtils.formatStat(item.stat.danmaku.toLong()),
                )
            }
        },
        coverContent = {
            AsyncImage(
                model = stationaryCoverRequest,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        },
        coverOverlayContent = {
            VideoStatusBadges(
                labels = item.statusLabels + listOfNotNull(sponsorStatusLabel),
                modifier = Modifier.align(Alignment.TopEnd).padding(AppSpacingTokens.ExtraSmall),
            )
            VideoCardCoverDurationText(
                text = if (progressState.progressSec > 0 || progressState.progressSec == -1) {
                    resolveHistoryProgressLabel(progressState.progressSec, item.duration)
                } else {
                    FormatUtils.formatDuration(item.duration)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(AppSpacingTokens.ExtraSmall),
            )
            if (progressState.showProgressBar) {
                AppLinearProgressIndicator(
                    progress = { progressState.progressFraction },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                )
            }
            PersonalCardSelectMask(selected = selected)
        },
        selected = selected,
        trailingContent = if (!batchMode && canRemove && onRemove != null) {
            {
                AppIconButton(
                    onClick = onRemove,
                ) {
                    if (stacked) {
                        // 堆叠卡：字形对齐锚点右下，落在日期行上；48dp 触控区不变。
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomEnd,
                        ) {
                            AppIcon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "移出收藏夹",
                                tint = AppContentColors.secondaryText(),
                            )
                        }
                    } else {
                        AppIcon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "移出收藏夹",
                            tint = AppContentColors.secondaryText(),
                        )
                    }
                }
            }
        } else null,
        onClick = triggerClick,
        onLongClick = onLongClick,
    )
}
