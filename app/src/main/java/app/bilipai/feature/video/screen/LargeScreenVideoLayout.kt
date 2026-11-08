package app.bilipai.feature.video.screen

import app.bilipai.feature.video.ambient.PlayerAmbientLayout
import app.bilipai.feature.video.danmaku.DanmakuManager
import android.content.res.Configuration
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.bilipai.core.store.SettingsManager
import app.bilipai.core.store.TabletSecondaryDefaultTab
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.LocalAnimatedVisibilityScope
import app.bilipai.core.ui.LocalSharedTransitionScope
import app.bilipai.core.ui.motion.AppMotionTokens
import app.bilipai.core.ui.transition.LocalVideoCardSharedElementSourceRoute
import app.bilipai.core.ui.transition.LocalVideoSharedTransitionSpeedSettings
import app.bilipai.core.ui.transition.LocalVideoTransitionAdaptiveInfo
import app.bilipai.core.ui.transition.resolveVideoCardSharedTransitionMotionSpec
import app.bilipai.core.ui.transition.resolveVideoSharedTransitionSourceCornerDp
import app.bilipai.core.ui.transition.videoCoverSharedElementKey
import app.bilipai.core.ui.transition.videoSharedElementBoundsTransformSpec
import app.bilipai.data.model.response.BgmInfo
import app.bilipai.data.model.response.ViewPoint
import app.bilipai.feature.video.progress.PbpProgressData
import app.bilipai.feature.video.state.VideoPlayerState
import app.bilipai.feature.video.ui.section.VideoPlayerSection
import app.bilipai.feature.video.ui.section.VideoPlayerSectionActions
import app.bilipai.feature.video.ui.section.VideoPlayerSectionState
import app.bilipai.feature.video.ui.section.resolveAllowLivePlayerSharedElementForMorph
import app.bilipai.feature.video.ui.section.resolveNavigationLiveSurfaceTextureEnabled
import app.bilipai.feature.video.viewmodel.CommentUiState
import app.bilipai.feature.video.viewmodel.SubReplyUiState
import app.bilipai.feature.video.viewmodel.VideoEngagementUiState
import app.bilipai.feature.video.viewmodel.VideoPlaybackUiState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun LargeScreenVideoLayout(
    playerState: VideoPlayerState,
    uiState: VideoPlaybackUiState,
    detailContentState: VideoPlaybackUiState.Success? = null,
    commentStateHandle: androidx.compose.runtime.State<CommentUiState>,
    engagementState: VideoEngagementUiState,
    subReplyStateHandle: androidx.compose.runtime.State<SubReplyUiState>,
    downloadProgress: androidx.compose.runtime.State<Float>,
    commentMemberDecorationsEnabled: Boolean,
    playbackActions: VideoDetailPlaybackActions,
    engagementActions: VideoDetailEngagementActions,
    commentActions: VideoDetailCommentActions,
    @Suppress("UNUSED_PARAMETER")
    configuration: Configuration,
    isVerticalVideo: Boolean,
    sleepTimerMinutes: Int?,
    viewPoints: List<ViewPoint>,
    pbpProgressData: PbpProgressData? = null,
    bvid: String,
    /** 详情页共享弹幕会话：不可在本层自行 acquire，否则开关会改到另一个实例。 */
    danmakuManager: DanmakuManager,
    coverUrl: String = "",
    onBack: () -> Unit,
    onUpClick: (Long) -> Unit,
    onBgmClick: (BgmInfo) -> Unit = {},
    onNavigateToAudioMode: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onPortraitFullscreen: () -> Unit,
    isInPipMode: Boolean,
    onPipClick: () -> Unit,
    isPortraitFullscreen: Boolean = false,
    onHomeClick: () -> Unit,
    currentCodec: String = "hev1",
    onCodecChange: (String) -> Unit = {},
    currentSecondCodec: String = "avc1",
    onSecondCodecChange: (String) -> Unit = {},
    currentAudioQuality: Int = -1,
    onAudioQualityChange: (Int) -> Unit = {},
    transitionEnabled: Boolean = false,
    onRelatedVideoClick: (String, android.os.Bundle?) -> Unit,
    showUpBadge: Boolean = true,
    onSearchKeywordClick: (String) -> Unit = {},
    onOpenBilibiliLink: ((String) -> Unit)? = null,
    currentPlayMode: app.bilipai.feature.video.player.PlayMode =
        app.bilipai.feature.video.player.PlayMode.SEQUENTIAL,
    onPlayModeClick: () -> Unit = {},
    forceCoverOnlyOnReturn: Boolean = false,
    predictiveBackCancelRecoveryGeneration: Int = 0,
    liveSurfaceCardTransitionEnabled: Boolean = true,
    isPlayerSurfaceReady: Boolean = true,
    paneControlsEnabled: Boolean = true,
    videoAiSummaryEntryEnabled: Boolean = true,
    videoNoteEnabled: Boolean = true,
    videoNoteDefaultCollapsed: Boolean = true,
    playerContent: (@Composable (Modifier) -> Unit)? = null,
    externalQueueTitle: String = "播放列表",
    externalQueueContent: (@Composable () -> Unit)? = null,
    transitionChromeAlphaProvider: () -> Float = { 1f },
) {
    val pageColor = AppSurfaceTokens.chromeBackground()
    // With a supplied transition host, backing pixels must move inside its media clip.
    // A background on the fixed split slot survives underneath the shrinking player.
    val playerBackdropModifier = if (playerContent == null) {
        Modifier.background(Color.Black)
    } else Modifier
    val danmakuChrome = rememberTabletDanmakuChromeState(danmakuManager)
    val context = LocalContext.current
    val secondaryDefaultTab by SettingsManager
        .getTabletSecondaryDefaultTab(context)
        .collectAsStateWithLifecycle(initialValue = TabletSecondaryDefaultTab.RELATED)
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(pageColor),
    ) {
        val windowWidthDp = maxWidth.value
        val windowHeightDp = maxHeight.value
        val metrics = remember(windowWidthDp, windowHeightDp, isVerticalVideo) {
            resolveLargeScreenVideoMetrics(
                windowWidthDp = windowWidthDp,
                windowHeightDp = windowHeightDp,
                isVerticalVideo = isVerticalVideo,
                enableVerticalExpand = true,
            )
        }
        val applySideStatusBarPadding =
            metrics.mode != LargeScreenVideoLayoutMode.AlmostSquare
        val showRelatedInIntro = resolveShowRelatedInIntro(metrics.mode)
        val relatedTabFirst = resolveRelatedTabFirstInSecondary(
            mode = metrics.mode,
            defaultTab = secondaryDefaultTab,
        )
        val includeRelatedTab = resolveIncludeRelatedTabInSecondary(metrics.mode)
        val success = detailContentState ?: (uiState as? VideoPlaybackUiState.Success)
        val player: @Composable (Modifier) -> Unit = { modifier ->
            LargeScreenPlayerHost(
                modifier = modifier,
                playerContent = playerContent,
                playerState = playerState,
                uiState = uiState,
                commentStateHandle = commentStateHandle,
                engagementState = engagementState,
                bvid = bvid,
                coverUrl = coverUrl,
                isVerticalVideo = isVerticalVideo,
                isInPipMode = isInPipMode,
                isPortraitFullscreen = isPortraitFullscreen,
                sleepTimerMinutes = sleepTimerMinutes,
                viewPoints = viewPoints,
                pbpProgressData = pbpProgressData,
                currentCodec = currentCodec,
                currentSecondCodec = currentSecondCodec,
                currentAudioQuality = currentAudioQuality,
                currentPlayMode = currentPlayMode,
                transitionEnabled = transitionEnabled,
                forceCoverOnlyOnReturn = forceCoverOnlyOnReturn,
                predictiveBackCancelRecoveryGeneration = predictiveBackCancelRecoveryGeneration,
                liveSurfaceCardTransitionEnabled = liveSurfaceCardTransitionEnabled,
                isPlayerSurfaceReady = isPlayerSurfaceReady,
                playbackActions = playbackActions,
                engagementActions = engagementActions,
                onBack = onBack,
                onHomeClick = onHomeClick,
                onToggleFullscreen = onToggleFullscreen,
                onPortraitFullscreen = onPortraitFullscreen,
                onPipClick = onPipClick,
                onNavigateToAudioMode = onNavigateToAudioMode,
                onCodecChange = onCodecChange,
                onSecondCodecChange = onSecondCodecChange,
                onAudioQualityChange = onAudioQualityChange,
                onPlayModeClick = onPlayModeClick,
                viewportWidthDpOverride = metrics.playerWidthDp.toInt(),
            )
        }
        val intro: @Composable (Modifier) -> Unit = { modifier ->
            if (success != null) {
                TabletVideoInfoPane(
                    currentPositionMsProvider = { playerState.player.currentPosition },
                    success = success,
                    engagementState = engagementState,
                    downloadProgress = downloadProgress,
                    playbackActions = playbackActions,
                    engagementActions = engagementActions,
                    onBgmClick = onBgmClick,
                    onRelatedVideoClick = onRelatedVideoClick,
                    onOpenBilibiliLink = onOpenBilibiliLink,
                    danmakuEnabled = danmakuChrome.enabled,
                    onDanmakuSendClick = playbackActions.showDanmakuSendDialog,
                    onDanmakuToggle = danmakuChrome.onToggle,
                    onOwnerUploadsClick = {
                        success.info.owner.mid.takeIf { it > 0L }?.let(onUpClick)
                    },
                    videoAiSummaryEntryEnabled = videoAiSummaryEntryEnabled,
                    videoNoteEnabled = videoNoteEnabled,
                    videoNoteDefaultCollapsed = videoNoteDefaultCollapsed,
                    modifier = modifier.videoDetailTransitionChrome(transitionChromeAlphaProvider),
                    showRelatedVideos = showRelatedInIntro,
                    showSupportingInfo = metrics.mode == LargeScreenVideoLayoutMode.VerticalThreePane,
                    onSearchKeywordClick = onSearchKeywordClick,
                )
            } else if (uiState is VideoPlaybackUiState.Loading) {
                app.bilipai.feature.video.ui.components.VideoDetailInfoPaneSkeleton(
                    modifier = modifier.videoDetailTransitionChrome(transitionChromeAlphaProvider),
                )
            }
        }
        val side: @Composable (Boolean) -> Unit = { includeIntro ->
            Box(Modifier.fillMaxSize().videoDetailTransitionChrome(transitionChromeAlphaProvider)) {
                if (success != null) {
                    TabletSecondaryContent(
                        success = success,
                        commentStateHandle = commentStateHandle,
                        subReplyStateHandle = subReplyStateHandle,
                        playbackActions = playbackActions,
                        engagementState = engagementState,
                        engagementActions = engagementActions,
                        commentActions = commentActions,
                        playerState = playerState,
                        onUpClick = onUpClick,
                        paneMode = TabletSecondaryPaneMode.EXPANDED,
                        onPaneModeChange = {},
                        onRelatedVideoClick = onRelatedVideoClick,
                        onSearchKeywordClick = onSearchKeywordClick,
                        showUpBadge = showUpBadge,
                        showIdentityDecorations = commentMemberDecorationsEnabled,
                        onOpenBilibiliLink = onOpenBilibiliLink,
                        externalQueueTitle = externalQueueTitle,
                        externalQueueContent = externalQueueContent,
                        requestedTabName = null,
                        onRequestedTabConsumed = {},
                        danmakuEnabled = danmakuChrome.enabled,
                        onDanmakuSendClick = playbackActions.showDanmakuSendDialog,
                        onDanmakuToggle = danmakuChrome.onToggle,
                        introContent = if (includeIntro) {
                            { intro(Modifier.fillMaxSize()) }
                        } else {
                            null
                        },
                        applyStatusBarPadding = applySideStatusBarPadding,
                        includeRelatedTab = includeRelatedTab,
                        includeOwnerUploadsTab = true,
                        relatedTabFirst = relatedTabFirst,
                    )
                } else if (uiState is VideoPlaybackUiState.Loading) {
                    app.bilipai.feature.video.ui.components.VideoDetailSecondaryPaneSkeleton(
                        showComments = !relatedTabFirst || !includeRelatedTab,
                        modifier = Modifier.fillMaxSize(),
                        showIntro = includeIntro,
                    )
                }
            }
        }
        when (metrics.mode) {
            LargeScreenVideoLayoutMode.VerticalThreePane -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .width(metrics.sidePaneWidthDp.dp)
                            .fillMaxHeight()
                            .statusBarsPadding(),
                    ) {
                        intro(Modifier.fillMaxSize())
                    }
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight()
                            .videoDetailTransitionChrome(transitionChromeAlphaProvider),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp,
                    )
                    player(
                        Modifier
                            .width(metrics.playerWidthDp.dp)
                            .height(metrics.playerHeightDp.dp)
                            .then(playerBackdropModifier),
                    )
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight()
                            .videoDetailTransitionChrome(transitionChromeAlphaProvider),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp,
                    )
                    Column(
                        modifier = Modifier
                            .width(metrics.sidePaneWidthDp.dp)
                            .fillMaxHeight(),
                    ) {
                        side(false)
                    }
                }
            }
            LargeScreenVideoLayoutMode.Split -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .width(metrics.playerWidthDp.dp)
                            .fillMaxHeight(),
                    ) {
                        player(
                            Modifier
                                .fillMaxWidth()
                                .height(metrics.playerHeightDp.dp)
                                .then(playerBackdropModifier),
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight()
                            .videoDetailTransitionChrome(transitionChromeAlphaProvider),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp,
                    )
                    Box(
                        modifier = Modifier
                            .width(metrics.sidePaneWidthDp.dp)
                            .fillMaxHeight(),
                    ) {
                        side(true)
                    }
                }
            }
            LargeScreenVideoLayoutMode.AlmostSquare -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    player(
                        Modifier
                            .fillMaxWidth()
                            .height(metrics.playerHeightDp.dp)
                            .then(playerBackdropModifier),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        ) {
                            intro(Modifier.fillMaxSize())
                        }
                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight()
                                .videoDetailTransitionChrome(transitionChromeAlphaProvider),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                            thickness = 1.dp,
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        ) {
                            side(false)
                        }
                    }
                }
            }
            LargeScreenVideoLayoutMode.Landscape -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .width(metrics.playerWidthDp.dp)
                            .fillMaxHeight(),
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            player(
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(LARGE_SCREEN_VIDEO_ASPECT_16_9)
                                    .then(playerBackdropModifier),
                            )
                            if (metrics.introBelowPlayer) {
                                intro(
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                )
                            }
                        }
                    }
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight()
                            .videoDetailTransitionChrome(transitionChromeAlphaProvider),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp,
                    )
                    Box(
                        modifier = Modifier
                            .width(metrics.sidePaneWidthDp.dp)
                            .fillMaxHeight(),
                    ) {
                        side(!metrics.introBelowPlayer)
                    }
                }
            }
            LargeScreenVideoLayoutMode.Phone -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .width(metrics.playerWidthDp.dp)
                            .fillMaxHeight(),
                    ) {
                        player(
                            Modifier
                                .fillMaxWidth()
                                .height(metrics.playerHeightDp.dp)
                                .then(playerBackdropModifier),
                        )
                        if (metrics.introBelowPlayer) {
                            intro(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .width(metrics.sidePaneWidthDp.dp)
                            .fillMaxHeight(),
                    ) {
                        side(!metrics.introBelowPlayer)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun LargeScreenPlayerHost(
    commentStateHandle: androidx.compose.runtime.State<app.bilipai.feature.video.viewmodel.CommentUiState>,
    modifier: Modifier,
    playerContent: (@Composable (Modifier) -> Unit)? = null,
    playerState: VideoPlayerState,
    uiState: VideoPlaybackUiState,
    engagementState: VideoEngagementUiState,
    bvid: String,
    coverUrl: String,
    isVerticalVideo: Boolean,
    isInPipMode: Boolean,
    isPortraitFullscreen: Boolean,
    sleepTimerMinutes: Int?,
    viewPoints: List<ViewPoint>,
    pbpProgressData: PbpProgressData? = null,
    currentCodec: String,
    currentSecondCodec: String,
    currentAudioQuality: Int,
    currentPlayMode: app.bilipai.feature.video.player.PlayMode,
    transitionEnabled: Boolean,
    forceCoverOnlyOnReturn: Boolean,
    predictiveBackCancelRecoveryGeneration: Int,
    liveSurfaceCardTransitionEnabled: Boolean,
    isPlayerSurfaceReady: Boolean,
    playbackActions: VideoDetailPlaybackActions,
    engagementActions: VideoDetailEngagementActions,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onPortraitFullscreen: () -> Unit,
    onPipClick: () -> Unit,
    onNavigateToAudioMode: () -> Unit,
    onCodecChange: (String) -> Unit,
    onSecondCodecChange: (String) -> Unit,
    onAudioQualityChange: (Int) -> Unit,
    onPlayModeClick: () -> Unit,
    viewportWidthDpOverride: Int? = null,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current
    val sourceRoute = LocalVideoCardSharedElementSourceRoute.current
    val sharedCoverShape = remember(sourceRoute) {
        RoundedCornerShape(resolveVideoSharedTransitionSourceCornerDp(sourceRoute).dp)
    }
    val sharedTransitionSpeedSettings = LocalVideoSharedTransitionSpeedSettings.current
    val transitionAdaptiveInfo = LocalVideoTransitionAdaptiveInfo.current
    val sharedTransitionMotionSpec = remember(
        sourceRoute,
        transitionEnabled,
        sharedTransitionSpeedSettings,
        transitionAdaptiveInfo,
    ) {
        resolveVideoCardSharedTransitionMotionSpec(
            sourceRoute = sourceRoute,
            transitionEnabled = transitionEnabled,
            speedSettings = sharedTransitionSpeedSettings,
            adaptiveInfo = transitionAdaptiveInfo,
        )
    }
    val playerContainerModifier = if (
        transitionEnabled &&
        sharedTransitionScope != null &&
        animatedVisibilityScope != null &&
        !forceCoverOnlyOnReturn
    ) {
        with(sharedTransitionScope) {
            modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(
                    key = videoCoverSharedElementKey(bvid),
                ),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { initialBounds, targetBounds ->
                    if (sharedTransitionMotionSpec.enabled) {
                        videoSharedElementBoundsTransformSpec(
                            motion = sharedTransitionMotionSpec,
                            initialBounds = initialBounds,
                            targetBounds = targetBounds,
                            durationMillis = sharedTransitionMotionSpec.durationMillis,
                        )
                    } else {
                        AppMotionTokens.spatialSpec()
                    }
                },
                clipInOverlayDuringTransition = OverlayClip(sharedCoverShape),
            )
        }
    } else {
        modifier
    }
    PlayerAmbientLayout(playerModifier = playerContainerModifier) {
        if (playerContent != null) {
            playerContent(Modifier.fillMaxSize())
        } else {
            VideoPlayerSection(
                state = VideoPlayerSectionState(
                    playerState = playerState,
                    uiState = uiState,
                    engagementState = engagementState,
                    commentStateHandle = commentStateHandle,
                    isLiked = engagementState.isLiked,
                    isDisliked = engagementState.isDisliked,
                    likeStatusKnown = engagementState.likeStatusKnown,
                    isFollowed = engagementState.isFollowing,
                    followStatusKnown = engagementState.followStatusKnown,
                    isFollowSubmitting = engagementState.isFollowSubmitting,
                    isTripleSubmitting = engagementState.isTripleSubmitting,
                    isCoined = engagementState.coinCount > 0,
                    isFavorited = engagementState.isFavorited,
                    isFullscreen = false,
                    isInPipMode = isInPipMode,
                    useTextureSurfaceForNavigation = resolveNavigationLiveSurfaceTextureEnabled(
                        cardTransitionEnabled = transitionEnabled,
                        liveSurfaceCardTransitionEnabled = liveSurfaceCardTransitionEnabled,
                    ),
                    isPlayerSurfaceReady = isPlayerSurfaceReady,
                    allowLivePlayerSharedElement = resolveAllowLivePlayerSharedElementForMorph(
                        cardTransitionEnabled = transitionEnabled,
                        liveSurfaceCardTransitionEnabled = liveSurfaceCardTransitionEnabled,
                    ),
                    predictiveBackCancelRecoveryGeneration = predictiveBackCancelRecoveryGeneration,
                    bvid = bvid,
                    coverUrl = coverUrl,
                    cdnCount = (uiState as? VideoPlaybackUiState.Success)?.cdnCount ?: 1,
                    cdnLineDiagnostics = (uiState as? VideoPlaybackUiState.Success)
                        ?.cdnLineDiagnostics.orEmpty(),
                    isCdnProbing = (uiState as? VideoPlaybackUiState.Success)?.isCdnProbing ?: false,
                    isAudioOnly = false,
                    sleepTimerMinutes = sleepTimerMinutes,
                    videoshotData = (uiState as? VideoPlaybackUiState.Success)?.videoshotData,
                    viewPoints = viewPoints,
                    pbpProgressData = pbpProgressData,
                    isVerticalVideo = isVerticalVideo,
                    isPortraitFullscreen = isPortraitFullscreen,
                    currentCodec = currentCodec,
                    currentSecondCodec = currentSecondCodec,
                    currentAudioQuality = currentAudioQuality,
                    currentPlayMode = currentPlayMode,
                    viewportWidthDpOverride = viewportWidthDpOverride,
                ),
                actions = VideoPlayerSectionActions(
                    resolveNextWatch = playbackActions.resolveNextWatch,
                    playNextWatch = playbackActions.playNextWatch,
                    onToggleFullscreen = onToggleFullscreen,
                    onQualityChange = playbackActions.changeQuality,
                    onBack = onBack,
                    onHomeClick = onHomeClick,
                    menuActions = playbackActions.menuActions,
                    onQuickFavorite = { engagementActions.onFavoriteAction(true) },
                    onDoubleTapLike = engagementActions.toggleLike,
                    onToggleFollow = engagementActions.toggleFollow,
                    onFollow = engagementActions.followOwner,
                    onToggleLike = engagementActions.toggleLike,
                    onDislike = engagementActions.toggleDislike,
                    onCoin = engagementActions.openCoinDialog,
                    onToggleFavorite = { engagementActions.onFavoriteAction(false) },
                    onTriple = engagementActions.doTripleAction,
                    onReloadVideo = playbackActions.reloadVideo,
                    onJumpToLastPlayedPart = playbackActions.jumpToLastPlayedPart,
                    onDismissPartResumeNotice = playbackActions.dismissPartResumeNotice,
                    onSwitchCdn = playbackActions.switchCdn,
                    onSwitchCdnTo = playbackActions.switchCdnTo,
                    onProbeCdnCandidates = playbackActions.probeCdnCandidates,
                    onAudioOnlyToggle = {
                        playbackActions.setAudioMode(true)
                        onNavigateToAudioMode()
                    },
                    onSleepTimerChange = playbackActions.setSleepTimer,
                    onPortraitFullscreen = onPortraitFullscreen,
                    onPipClick = onPipClick,
                    onCodecChange = onCodecChange,
                    onSecondCodecChange = onSecondCodecChange,
                    onAudioQualityChange = onAudioQualityChange,
                    onPlaybackSpeedChange = playbackActions.applyPlaybackSpeed,
                    onAudioLangChange = playbackActions.changeAudioLanguage,
                    onSaveCover = playbackActions.saveCover,
                    onDownloadAudio = playbackActions.downloadAudio,
                    onPlayModeClick = onPlayModeClick,
                    onSubtitleTrackSelected = playbackActions.selectSubtitleTrack,
                    onDanmakuInputClick = playbackActions.showDanmakuSendDialog,
                    onLikeDanmaku = playbackActions.likeDanmaku,
                    onLikeDanmakuToggle = playbackActions.likeDanmakuToggle,
                    likedDanmakuIds = playbackActions.likedDanmakuIds,
                    onReportDanmaku = playbackActions.reportDanmaku,
                    onRecallDanmaku = playbackActions.recallDanmaku,
                ),
            )
        }
    }
}
