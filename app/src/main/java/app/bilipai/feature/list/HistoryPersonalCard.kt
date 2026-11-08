package app.bilipai.feature.list

import app.bilipai.core.ui.components.VideoStatusBadges
import app.bilipai.core.ui.components.wallpaperAwareMediaCardBackground
import app.bilipai.core.ui.AppContentColors

import app.bilipai.feature.personal.PersonalMediaCardFrame
import app.bilipai.feature.personal.rememberPersonalCardVideoTransition
import coil3.request.crossfade

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import app.bilipai.core.ui.transition.videoCardSourceBoundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.AppSpacingTokens
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.ContainerLevel
import app.bilipai.core.ui.FeedTitleHierarchy
import app.bilipai.core.ui.LocalAnimatedVisibilityScope
import app.bilipai.core.ui.LocalSharedTransitionScope
import app.bilipai.core.ui.MediaContrastPalette
import app.bilipai.core.ui.components.AppWindowAction
import app.bilipai.core.ui.components.AppWindowActionMenu
import app.bilipai.core.ui.components.AppIcon
import app.bilipai.core.ui.components.AppIconButton
import app.bilipai.core.ui.components.AppLinearProgressIndicator
import app.bilipai.core.ui.components.AppText
import app.bilipai.core.ui.feedContentTypography
import app.bilipai.core.ui.skeleton.ContentSkeletonBlock
import app.bilipai.core.ui.skeleton.rememberContentSkeletonBlockColor
import app.bilipai.core.ui.skeleton.rememberContentSkeletonPulseState
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
import app.bilipai.data.model.response.HistoryBusiness
import app.bilipai.data.model.response.HistoryItem
import app.bilipai.feature.home.components.cards.resolveVideoCardCoverOverlayTextShadow
import app.bilipai.feature.home.components.cards.HorizontalVideoCardFrame
import app.bilipai.feature.plugin.rememberSponsorFeedBadgeLabel
import app.bilipai.feature.personal.PersonalCardSelectMask

internal fun resolveHistoryKindLabel(business: HistoryBusiness): String = when (business) {
    HistoryBusiness.ARCHIVE -> "视频"
    HistoryBusiness.PGC -> "番剧"
    HistoryBusiness.CHEESE -> "课堂"
    HistoryBusiness.LIVE -> "直播"
    HistoryBusiness.ARTICLE -> "专栏"
    HistoryBusiness.UNKNOWN -> "未知"
}

internal fun resolveHistoryProgressLabel(progress: Int, duration: Int): String =
    app.bilipai.data.model.resolveHistoryProgressLabel(progress, duration)

internal fun canAddHistoryToWatchLater(item: HistoryItem): Boolean =
    item.business == HistoryBusiness.ARCHIVE && item.videoItem.id > 0L

@Composable
internal fun HistoryPersonalCardSkeleton(
    modifier: Modifier = Modifier,
    blockColor: (() -> Color)? = null,
    stacked: Boolean = false,
) {
    val color = blockColor ?: rememberContentSkeletonBlockColor(rememberContentSkeletonPulseState())
    val blockShape = AppShapes.container(ContainerLevel.Card)

    HorizontalVideoCardFrame(
        stacked = stacked,
        fillInfoHeight = !stacked,
        modifier = modifier
            .fillMaxWidth(),
        coverContent = {
            ContentSkeletonBlock(
                color = color,
                shape = blockShape,
                modifier = Modifier.fillMaxSize(),
            )
        },
        infoContent = {
            ContentSkeletonBlock(
                color = color,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            ContentSkeletonBlock(
                color = color,
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(16.dp),
            )
        },
        supportingContent = {
            ContentSkeletonBlock(
                color = color,
                modifier = Modifier.fillMaxWidth(0.68f).height(12.dp),
            )
            ContentSkeletonBlock(
                color = color,
                modifier = Modifier.fillMaxWidth(0.48f).height(12.dp),
            )
        },
        trailingContent = {
            ContentSkeletonBlock(
                color = color,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 2.dp, bottom = 2.dp).size(17.dp),
            )
        },
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun HistoryPersonalCard(
    item: HistoryItem,
    selected: Boolean,
    batchMode: Boolean,
    transitionEnabled: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onUpClick: (() -> Unit)?,
    onAddToWatchLater: (() -> Unit)?,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    stacked: Boolean = false,
) {
    val video = item.videoItem
    val context = LocalContext.current
    val sponsorStatusLabel = rememberSponsorFeedBadgeLabel(video.bvid)
    val coverShape = AppShapes.mediaCover()
    val containerTarget = if (batchMode) null else resolveHistoryCardContainerTarget(item)
    val usesVideoTransition = !batchMode && containerTarget == null && item.business != HistoryBusiness.ARTICLE
    val transition = rememberPersonalCardVideoTransition(
        bvid = video.bvid,
        transitionEnabled = transitionEnabled && usesVideoTransition,
        clipShape = coverShape,
    )
    val progressState = remember(item.progress, video.duration, video.view_at) {
        resolveVideoDisplayProgressState(
            serverProgressSec = item.progress,
            durationSec = video.duration,
            viewAt = video.view_at,
        )
    }
    val stationaryCoverUrl = remember(video.pic) { FormatUtils.fixImageUrl(video.pic) }
    val stationaryCoverRequest = remember(stationaryCoverUrl) {
        ImageRequest.Builder(context)
            .data(stationaryCoverUrl)
            .crossfade(false)
            .memoryCacheKey(stationaryCoverUrl)
            .diskCacheKey(stationaryCoverUrl)
            .build()
    }
    val legacyTriggerClick = {
        if (transitionEnabled && usesVideoTransition) {
            transition.recordPosition(
                bvid = video.bvid,
                stacked = stacked,
                sourceCornerDp = 12,
                chrome = VideoCardSourceChromeSnapshot(
                    title = video.title,
                    ownerName = video.owner.name.takeIf { it.isNotBlank() }
                        ?: if (item.business == HistoryBusiness.PGC) "番剧" else "未知作者",
                    ownerFaceUrl = video.owner.face,
                    viewText = FormatUtils.formatStat(video.stat.view.toLong()),
                    danmakuText = FormatUtils.formatStat(video.stat.danmaku.toLong()),
                    durationText = FormatUtils.formatDuration(video.duration),
                    infoPresentation = app.bilipai.core.ui.transition
                        .resolveVideoCardSourceInfoPresentation(
                            publishTimeText = FormatUtils.formatHistoryViewTime(video.view_at),
                            // History cards show owner + viewed time, not play/danmaku stats.
                            showStatsInInfo = false,
                            ownerBeforePublish = true,
                            showOverflowMenu = !batchMode,
                        ),
                    coverPresentation = VideoCardSourceCoverPresentation(
                        showDurationOnCover = true,
                        showHistoryProgressBar = progressState.showProgressBar,
                        historyProgressFraction = progressState.progressFraction,
                    ),
                    coverUrl = stationaryCoverUrl,
                    coverCacheKey = stationaryCoverUrl,
                ),
            )
        }
        onClick()
    }

    val contentContainer = containerTarget?.let {
        app.bilipai.core.ui.rememberNativeCardContainerTransition(
            target = it,
            onClick = legacyTriggerClick,
            enabled = transitionEnabled,
        )
    }
    val triggerClick = contentContainer?.onClick ?: legacyTriggerClick

    val coverOverlayTextStyle = remember {
        TextStyle(shadow = resolveVideoCardCoverOverlayTextShadow())
    }
    val contentTypography = app.bilipai.feature.home.components.cards.horizontalVideoCardTypography()
    val owner = video.owner.name.takeIf { it.isNotBlank() }
        ?: if (item.business == HistoryBusiness.PGC) "番剧" else "未知作者"
    val viewedAt = FormatUtils.formatHistoryViewTime(video.view_at)
    val titleMaxLines = app.bilipai.core.ui.videoCardTitleMaxLines()

    val actionContent: (@Composable () -> Unit)? = if (batchMode) null else {
        {
            Box(
                contentAlignment = Alignment.BottomCenter,
            ) {
                AppWindowActionMenu(
                    groups = listOf(
                        buildList {
                            if (onUpClick != null) {
                                add(AppWindowAction(
                                    label = "访问UP主",
                                    icon = Icons.Outlined.PersonOutline,
                                    onClick = onUpClick,
                                ))
                            }
                            if (onAddToWatchLater != null) {
                                add(AppWindowAction(
                                    label = "加入稍后再看",
                                    icon = Icons.Outlined.WatchLater,
                                    onClick = onAddToWatchLater,
                                ))
                            }
                        },
                        listOf(
                            AppWindowAction(
                                label = "删除记录",
                                icon = Icons.Outlined.DeleteOutline,
                                iconTint = AppContentColors.error(),
                                onClick = onDelete,
                            ),
                        ),
                    ),
                    modifier = Modifier.size(29.dp),
                    // 菜单源使用紧凑锚点；可见字形由外层
                    // 严格叠放在锚点右下角，绕开 IconButton 内容居中/裁切的内部布局。
                    anchorModifier = Modifier.fillMaxSize(),
                ) {
                    Spacer(Modifier)
                }
                AppIcon(
                    Icons.Outlined.MoreVert,
                    contentDescription = "历史记录操作",
                    tint = AppContentColors.secondaryText(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 2.dp, bottom = 2.dp)
                        .size(17.dp),
                )
            }
        }
    }
    if (stacked) {
        PersonalMediaCardFrame(
            stacked = true,
            selected = selected,
            modifier = modifier
                .then(contentContainer?.modifier ?: transition.shellModifier)
                .onGloballyPositioned { transition.bounds.cardBounds = it.videoCardSourceBoundsInRoot() },
            nativeSnapshotModifier = transition.nativeCardSnapshot.modifier,
            coverOverlayModifier = transition.nativeCardSnapshot.coverOverlayModifier,
            coverModifier = Modifier.onGloballyPositioned {
                transition.bounds.coverBounds = it.videoCardSourceBoundsInRoot()
            },
            headlineContent = {
                AppText(text = video.title, style = contentTypography.title, maxLines = titleMaxLines, overflow = app.bilipai.core.ui.videoCardTitleOverflow())
            },
            supportingContent = {
                AppText(text = owner, style = contentTypography.author, maxLines = 1, overflow = TextOverflow.Ellipsis)
                AppText(text = viewedAt, style = contentTypography.author)
            },
            coverContent = {
                AsyncImage(model = stationaryCoverRequest, contentDescription = null,
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            },
            coverOverlayContent = {
                VideoStatusBadges(
                    labels = video.statusLabels + listOfNotNull(sponsorStatusLabel),
                    modifier = Modifier.align(Alignment.TopEnd).padding(AppSpacingTokens.ExtraSmall),
                )
                AppText(
                    text = resolveHistoryProgressLabel(item.progress, video.duration),
                    color = MediaContrastPalette.Foreground,
                    style = contentTypography.coverBadge.merge(coverOverlayTextStyle),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp),
                )
                if (progressState.showProgressBar) {
                    AppLinearProgressIndicator(progress = { progressState.progressFraction },
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth())
                }
                PersonalCardSelectMask(selected = selected)
            },
            trailingContent = actionContent?.let { action -> { action() } },
            onClick = triggerClick,
            onLongClick = onLongClick,
        )
        return
    }

    HorizontalVideoCardFrame(
        fillInfoHeight = true,
        modifier = modifier
            .fillMaxWidth()
            .then(contentContainer?.modifier ?: transition.shellModifier)
            .clip(coverShape)
            .then(transition.nativeCardSnapshot.modifier)
            .wallpaperAwareMediaCardBackground(AppSurfaceTokens.cardContainer(), coverShape)
            .combinedClickable(
                onClick = triggerClick,
                onLongClick = onLongClick,
            )
            .onGloballyPositioned { transition.bounds.cardBounds = it.videoCardSourceBoundsInRoot() },
        coverAspectRatio = app.bilipai.core.ui.videoCardCoverAspectRatio(),
        coverModifier = Modifier.onGloballyPositioned {
            transition.bounds.coverBounds = it.videoCardSourceBoundsInRoot()
        },
        coverOverlayModifier = transition.nativeCardSnapshot.coverOverlayModifier,
        coverContent = {
            AsyncImage(
                model = stationaryCoverRequest,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        },
        coverOverlayContent = {
            VideoStatusBadges(
                labels = video.statusLabels + listOfNotNull(sponsorStatusLabel),
                modifier = Modifier.align(Alignment.TopEnd).padding(AppSpacingTokens.ExtraSmall),
            )
            AppText(
                text = resolveHistoryProgressLabel(progressState.progressSec, video.duration),
                color = MediaContrastPalette.Foreground,
                style = feedContentTypography().coverBadge
                    .copy(fontWeight = FontWeight.Medium)
                    .merge(coverOverlayTextStyle),
                maxLines = 1,
                tapToCopyEnabled = false,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 6.dp, end = 6.dp, bottom = 8.dp),
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
        infoContent = {
            AppText(
                text = video.title,
                modifier = Modifier.fillMaxWidth(),
                style = contentTypography.title,
                maxLines = titleMaxLines,
                overflow = app.bilipai.core.ui.videoCardTitleOverflow(),
                tapToCopyEnabled = false,
            )
        },
        supportingContent = {
            AppText(
                text = owner,
                style = contentTypography.author,
                color = AppContentColors.secondaryText(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                tapToCopyEnabled = false,
            )
            if (viewedAt.isNotBlank()) {
                AppText(
                    text = viewedAt,
                    style = contentTypography.author,
                    color = AppContentColors.secondaryText(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    tapToCopyEnabled = false,
                )
            }
        },
        trailingContent = actionContent?.let { action -> { action() } },
    )
}
