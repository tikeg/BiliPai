package app.bilipai.feature.video.screen

import app.bilipai.core.util.rememberHapticFeedback
import app.bilipai.core.util.HapticType

import app.bilipai.core.ui.AppContentColors
import app.bilipai.core.ui.resolveFilledButtonContainerColor
import app.bilipai.core.ui.resolveFilledButtonContentColor
import app.bilipai.core.ui.components.AppHorizontalDivider

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.OrientationEventListener
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import app.bilipai.core.ui.components.AppLazyColumn as LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ButtonDefaults
import app.bilipai.core.ui.components.AppIcon
import androidx.compose.material3.MaterialTheme
import app.bilipai.core.ui.components.AppText
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import app.bilipai.core.ui.motion.AppMotionTokens
import app.bilipai.core.ui.LocalComponentMotionEnabled
import app.bilipai.core.ui.components.AppIconButton
import app.bilipai.core.ui.rememberAppChevronDownIcon
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.ui.layout.ContentScale
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import app.bilipai.data.model.response.BgmInfo
import app.bilipai.data.model.CommentFraudStatus
import app.bilipai.data.repository.resolveCommentFraudLightMessage
import app.bilipai.data.repository.shouldShowCommentFraudResultDialog
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.Player
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.bilipai.core.ui.blur.rememberRecoverableHazeState
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.ContainerLevel
import app.bilipai.core.ui.appContentDialogWidth
import app.bilipai.core.store.PortraitPlayerCollapseMode
//  已改用 AppContentColors.accent()

import app.bilipai.data.model.response.RelatedVideo
import app.bilipai.data.model.response.ReplyItem
import app.bilipai.data.model.response.UgcSeason
import app.bilipai.data.model.response.VideoTag
import app.bilipai.data.model.response.ViewInfo
import app.bilipai.data.model.response.ViewPoint
import app.bilipai.feature.common.resolveIndexedVideoLazyKey
// Refactored UI components
import app.bilipai.feature.video.ui.section.VideoTitleSection
import app.bilipai.feature.video.ui.section.VideoTitleWithDesc
import app.bilipai.feature.video.ui.section.UpInfoSection
import app.bilipai.feature.video.ui.section.DescriptionSection
import app.bilipai.feature.video.ui.section.ActionButtonsRow
import app.bilipai.feature.video.ui.section.ActionButton
import app.bilipai.feature.video.ui.components.resolveDanmakuTimestampJumpMs
import app.bilipai.feature.video.ui.components.RelatedVideosHeader
import app.bilipai.feature.video.ui.components.RelatedVideoItem
import app.bilipai.feature.video.ui.components.CoinDialog
import app.bilipai.feature.video.ui.components.CollectionRow
import app.bilipai.feature.video.ui.components.CollectionSheet
import app.bilipai.feature.video.ui.components.PagesSelector
// Imports for moved classes
import app.bilipai.feature.video.viewmodel.VideoPlaybackViewModel
import app.bilipai.feature.video.viewmodel.VideoPlaybackUiState
import app.bilipai.feature.video.viewmodel.VideoComposerViewModel
import app.bilipai.feature.video.viewmodel.VideoEngagementViewModel
import app.bilipai.feature.video.viewmodel.VideoEngagementEvent
import app.bilipai.feature.video.viewmodel.VideoSupplementViewModel
import app.bilipai.feature.video.viewmodel.toEngagementSeed
import app.bilipai.feature.video.viewmodel.toSupplementSeed
import app.bilipai.feature.video.viewmodel.QualitySwitchFailureDialogState
import app.bilipai.feature.video.viewmodel.CommentUiState
import app.bilipai.feature.video.viewmodel.VideoCommentViewModel
import app.bilipai.feature.video.state.VideoPlayerState
import app.bilipai.feature.video.state.rememberVideoPlayerState
import app.bilipai.feature.video.state.shouldReuseMiniPlayerAtEntry
import app.bilipai.feature.video.ui.section.VideoPlayerSection
import app.bilipai.feature.video.ui.section.shouldKeepVideoPlaybackAwake
import app.bilipai.feature.video.ui.components.ReplyHeader
import app.bilipai.feature.video.ui.components.ReplyItemView
import app.bilipai.feature.video.ui.components.CommentFraudResultDialog
import app.bilipai.feature.video.ui.components.VideoCommentSheetHost

import app.bilipai.feature.video.viewmodel.CommentSortMode  //  新增
import app.bilipai.feature.video.ui.components.LikeBurstAnimation
import app.bilipai.feature.video.ui.components.TripleSuccessAnimation
import app.bilipai.feature.video.ui.components.VideoDetailSkeleton
import app.bilipai.feature.video.ui.components.VideoActionFeedbackHost
import app.bilipai.feature.video.subtitle.SubtitleAutoPreference
import app.bilipai.feature.video.subtitle.SubtitleDisplayMode
import app.bilipai.feature.video.subtitle.resolveSubtitleDisplayModePreference
import app.bilipai.feature.video.progress.PbpProgressData
import app.bilipai.feature.video.usecase.playPlayerFromUserAction
import app.bilipai.feature.video.usecase.seekPlayerFromUserAction
import app.bilipai.feature.video.policy.reduceVideoDetailPostScroll
import app.bilipai.feature.video.policy.reduceVideoDetailPreScroll
import app.bilipai.feature.video.policy.resolveVideoDetailCollapseProgress
import app.bilipai.feature.video.subtitle.resolveSubtitlePreferenceSession
import app.bilipai.core.ui.AdaptiveLoadingIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
//  共享元素过渡
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.shape.RoundedCornerShape
import app.bilipai.core.ui.LocalPredictiveBackGestureEnabled
import app.bilipai.core.ui.LocalSharedTransitionScope
import app.bilipai.core.ui.LocalAnimatedVisibilityScope
import app.bilipai.core.ui.transition.LocalVideoSharedTransitionSpeedSettings
import app.bilipai.core.ui.transition.VideoSharedTransitionPlaybackIntent
import app.bilipai.core.ui.transition.resolveVideoCardSharedTransitionMotionSpec
import app.bilipai.core.ui.transition.resolveVideoCardSharedTransitionEnterEasing
import app.bilipai.core.ui.transition.resolveVideoCardSharedTransitionReturnEasing
import app.bilipai.core.ui.transition.resolveVideoSharedCoverCacheKey
import app.bilipai.core.ui.transition.resolveVideoSharedTransitionPlaybackIntent
import app.bilipai.core.ui.transition.resolveVideoSharedTransitionSourceCornerDp
import app.bilipai.core.ui.transition.resolveVideoSharedTransitionVisualSpec
import app.bilipai.core.ui.transition.shouldEnableVideoCoverSharedTransition
import app.bilipai.core.ui.transition.shouldUseVideoCardShellContainerTransform
import app.bilipai.core.ui.transition.videoSharedElementBoundsTransformSpec
import app.bilipai.core.ui.rememberAppCollectionIcon
import app.bilipai.core.ui.rememberAppDownloadIcon
import app.bilipai.core.ui.rememberAppMusicIcon
import app.bilipai.core.ui.rememberAppPhotoIcon
import app.bilipai.core.ui.rememberAppPlayIcon
import app.bilipai.feature.video.player.MiniPlayerManager
import app.bilipai.feature.video.player.PlaybackService
import app.bilipai.feature.video.player.PlaylistItem
import app.bilipai.feature.video.player.PlaylistManager
import app.bilipai.feature.video.player.PlaylistUiState
import app.bilipai.feature.video.player.ExternalPlaylistSource
import app.bilipai.feature.video.player.buildPipPlaybackRemoteActions
import app.bilipai.core.ui.performance.TrackJankStateFlag
import app.bilipai.feature.video.ui.overlay.PlayerProgress
import app.bilipai.feature.video.ui.components.VideoAspectRatio
import app.bilipai.feature.video.danmaku.rememberDanmakuManager
import app.bilipai.core.ui.blur.shouldAllowRuntimeShaderBackedHazeEffect
import app.bilipai.core.ui.blur.unifiedBlur
import app.bilipai.core.ui.AppAlertDialog
import app.bilipai.core.ui.AppModalBottomSheet
import app.bilipai.core.ui.components.AppButton
import app.bilipai.core.ui.components.AppCheckbox
import app.bilipai.core.ui.components.AppCircularProgressIndicator
import app.bilipai.core.ui.components.AppSurface
import app.bilipai.core.ui.components.AppTextButton
import app.bilipai.core.util.CardPositionManager
import app.bilipai.core.util.FormatUtils
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import app.bilipai.core.ui.blur.hazeEffectCompat
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import app.bilipai.feature.video.ui.components.DanmakuContextMenu
import app.bilipai.feature.video.ui.components.DanmakuBlockActionTarget
import app.bilipai.feature.video.ui.components.resolveDanmakuBlockActionFeedbackMessage
import app.bilipai.feature.video.danmaku.appendDanmakuKeywordBlockRule
import app.bilipai.feature.video.ui.components.InteractiveChoiceOverlay
import app.bilipai.feature.video.ui.feedback.VideoFeedbackAnchor
import app.bilipai.feature.video.ui.feedback.TripleCelebrationPlacement
import app.bilipai.feature.video.ui.feedback.resolveQualityReminderPlacement
import app.bilipai.feature.video.ui.feedback.resolveTripleCelebrationPlacement
import app.bilipai.feature.video.ui.feedback.resolveVideoFeedbackPlacement
import app.bilipai.feature.video.ui.section.resolveForcedReturnCoverSharedElementSourceRoute
import app.bilipai.feature.video.share.VideoSharePayload
import app.bilipai.feature.video.share.VideoShareSheet
import app.bilipai.feature.video.viewmodel.PlayerToastMessage
import app.bilipai.feature.video.viewmodel.PlayerToastPresentation
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun VideoDetailFollowGroupDialog(
    viewModel: VideoPlaybackViewModel
) {
    val followGroupDialogVisible by viewModel.followGroupDialogVisible.collectAsStateWithLifecycle(
        context = kotlin.coroutines.EmptyCoroutineContext
    )
    val followGroupTags by viewModel.followGroupTags.collectAsStateWithLifecycle(
        context = kotlin.coroutines.EmptyCoroutineContext
    )
    val followGroupSelectedTagIds by viewModel.followGroupSelectedTagIds.collectAsStateWithLifecycle(
        context = kotlin.coroutines.EmptyCoroutineContext
    )
    val isFollowGroupsLoading by viewModel.isFollowGroupsLoading.collectAsStateWithLifecycle(
        context = kotlin.coroutines.EmptyCoroutineContext
    )
    val isSavingFollowGroups by viewModel.isSavingFollowGroups.collectAsStateWithLifecycle(
        context = kotlin.coroutines.EmptyCoroutineContext
    )
    if (!followGroupDialogVisible) return

    AppAlertDialog(
        onDismissRequest = {
            if (!isSavingFollowGroups) viewModel.dismissFollowGroupDialog()
        },
        title = { AppText("设置关注分组") },
        text = {
            if (isFollowGroupsLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AdaptiveLoadingIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (followGroupTags.isEmpty()) {
                        AppText(
                            text = "暂无可用分组（不勾选即为默认分组）",
                            color = AppContentColors.secondaryText(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        followGroupTags.forEach { tag ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleFollowGroupSelection(tag.tagid) }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppCheckbox(
                                    checked = followGroupSelectedTagIds.contains(tag.tagid),
                                    onCheckedChange = { viewModel.toggleFollowGroupSelection(tag.tagid) }
                                )
                                AppText(
                                    text = "${tag.name} (${tag.count})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppContentColors.primaryText()
                                )
                            }
                        }
                    }
                    AppText(
                        text = "可多选，确定后覆盖原分组设置。",
                        color = AppContentColors.secondaryText(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            AppButton(
                onClick = { viewModel.saveFollowGroupSelection() },
                enabled = !isFollowGroupsLoading && !isSavingFollowGroups
            ) {
                if (isSavingFollowGroups) {
                    AppCircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = AppContentColors.onAccent()
                    )
                } else {
                    AppText("确定")
                }
            }
        },
        dismissButton = {
            AppTextButton(
                onClick = { viewModel.dismissFollowGroupDialog() },
                enabled = !isSavingFollowGroups
            ) {
                AppText("取消")
            }
        }
    )
}

@Composable
internal fun VideoDetailPlaybackEndedDialog(
    viewModel: VideoPlaybackViewModel,
    player: Player
) {
    val showPlaybackEndedDialog by viewModel.showPlaybackEndedDialog.collectAsStateWithLifecycle(
        context = kotlin.coroutines.EmptyCoroutineContext
    )
    if (!showPlaybackEndedDialog) return

    val dialogLayout = remember {
        app.bilipai.core.ui.resolveAppContentDialogLayoutPolicy(maxWidthDp = 420)
    }
    androidx.compose.ui.window.Dialog(
        onDismissRequest = { viewModel.dismissPlaybackEndedDialog() },
        properties = app.bilipai.core.ui.resolveAppContentDialogProperties(
            usePlatformDefaultWidth = dialogLayout.usePlatformDefaultWidth,
        ),
    ) {
        app.bilipai.core.ui.ModalWindowBlurBehindEffect()
        AppSurface(
            modifier = Modifier.appContentDialogWidth(policy = dialogLayout),
            shape = AppShapes.container(ContainerLevel.Dialog),
            color = AppSurfaceTokens.surface(),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppText(
                    text = "播放完成",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                AppText(
                    text = "选择接下来的操作",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppContentColors.secondaryText()
                )
                AppButton(
                    onClick = {
                        viewModel.dismissPlaybackEndedDialog()
                        player.seekTo(0)
                        playPlayerFromUserAction(player)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = AppContentColors.primaryText()
                    )
                ) {
                    AppText("🔄 重播当前视频")
                }
                AppButton(
                    onClick = {
                        viewModel.dismissPlaybackEndedDialog()
                        viewModel.playNextRecommended()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = resolveFilledButtonContainerColor(MaterialTheme.colorScheme),

                        contentColor = resolveFilledButtonContentColor(MaterialTheme.colorScheme)
                    )
                ) {
                    AppText("▶️ 播放下一个视频")
                }
                AppTextButton(
                    onClick = { viewModel.dismissPlaybackEndedDialog() }
                ) {
                    AppText("暂不操作")
                }
            }
        }
    }
}

@Composable
internal fun VideoDetailQualitySwitchFailureDialog(
    context: Context,
    viewModel: VideoPlaybackViewModel,
    qualitySwitchFailureDialog: QualitySwitchFailureDialogState?,
    qualitySwitchFailureDialogEnabled: Boolean,
    qualitySwitchFailureDialogOnceEnabled: Boolean,
    qualitySwitchFailureDialogShown: Boolean,
    playerDiagnosticLoggingEnabled: Boolean,
    qualitySwitchDialogScope: CoroutineScope
) {
    LaunchedEffect(
        qualitySwitchFailureDialog?.requestedQualityId,
        qualitySwitchFailureDialogEnabled,
        qualitySwitchFailureDialogOnceEnabled,
        qualitySwitchFailureDialogShown
    ) {
        val dialog = qualitySwitchFailureDialog ?: return@LaunchedEffect
        val shouldSuppressDialog = !qualitySwitchFailureDialogEnabled ||
            (qualitySwitchFailureDialogOnceEnabled && qualitySwitchFailureDialogShown)
        if (shouldSuppressDialog) {
            viewModel.dismissQualitySwitchFailureDialog()
        }
    }

    qualitySwitchFailureDialog
        ?.takeIf {
            qualitySwitchFailureDialogEnabled &&
                !(qualitySwitchFailureDialogOnceEnabled && qualitySwitchFailureDialogShown)
        }
        ?.let { dialog ->
            var onceForCurrentDialog by remember(dialog) {
                mutableStateOf(qualitySwitchFailureDialogOnceEnabled)
            }
            LaunchedEffect(qualitySwitchFailureDialogOnceEnabled) {
                onceForCurrentDialog = qualitySwitchFailureDialogOnceEnabled
            }

            fun dismissQualitySwitchFailureDialogAfterUserChoice() {
                qualitySwitchDialogScope.launch {
                    if (onceForCurrentDialog) {
                        app.bilipai.core.store.SettingsManager
                            .markQualitySwitchFailureDialogShown(context)
                    }
                    viewModel.dismissQualitySwitchFailureDialog()
                }
            }

            AppAlertDialog(
                onDismissRequest = { dismissQualitySwitchFailureDialogAfterUserChoice() },
                title = { AppText(dialog.title) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppText(dialog.message)
                        AppTextButton(
                            onClick = {
                                qualitySwitchDialogScope.launch {
                                    app.bilipai.core.store.SettingsManager
                                        .setPlayerDiagnosticLoggingEnabled(
                                            context,
                                            !playerDiagnosticLoggingEnabled
                                        )
                                }
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            AppText(
                                if (playerDiagnosticLoggingEnabled) {
                                    "关闭诊断日志"
                                } else {
                                    "开启诊断日志"
                                }
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clickable {
                                    val nextValue = !onceForCurrentDialog
                                    onceForCurrentDialog = nextValue
                                    qualitySwitchDialogScope.launch {
                                        app.bilipai.core.store.SettingsManager
                                            .setQualitySwitchFailureDialogOnceEnabled(context, nextValue)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                text = "仅提示一次",
                                modifier = Modifier.weight(1f),
                                tapToCopyEnabled = false
                            )
                            AppCheckbox(
                                checked = onceForCurrentDialog,
                                onCheckedChange = { checked ->
                                    onceForCurrentDialog = checked
                                    qualitySwitchDialogScope.launch {
                                        app.bilipai.core.store.SettingsManager
                                            .setQualitySwitchFailureDialogOnceEnabled(context, checked)
                                    }
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    AppTextButton(
                        onClick = {
                            app.bilipai.core.util.LogCollector.exportAndShare(context)
                            dismissQualitySwitchFailureDialogAfterUserChoice()
                        }
                    ) {
                        AppText("导出日志")
                    }
                },
                dismissButton = {
                    AppTextButton(onClick = { dismissQualitySwitchFailureDialogAfterUserChoice() }) {
                        AppText("关闭")
                    }
                }
            )
        }
}

@Composable
internal fun VideoDetailDanmakuContextMenu(
    context: Context,
    viewModel: VideoPlaybackViewModel,
    activeDanmakuBlockRulesRaw: String,
    activeDanmakuScope: app.bilipai.core.store.DanmakuSettingsScope,
    sortPreferenceScope: CoroutineScope
) {
    val danmakuMenuState by viewModel.danmakuMenuState.collectAsStateWithLifecycle(
        context = kotlin.coroutines.EmptyCoroutineContext
    )
    val playbackUiState by viewModel.uiState.collectAsStateWithLifecycle(
        context = kotlin.coroutines.EmptyCoroutineContext
    )
    if (!danmakuMenuState.visible) return
    val menuLifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(menuLifecycle, viewModel) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) viewModel.hideDanmakuMenu()
        }
        menuLifecycle.addObserver(observer)
        onDispose { menuLifecycle.removeObserver(observer) }
    }
    val playback = playbackUiState as? app.bilipai.feature.video.viewmodel.VideoPlaybackUiState.Success
    val menuIdentity = remember(danmakuMenuState.dmid, danmakuMenuState.text, danmakuMenuState.userHash) {
        playback?.info?.let { it.bvid to it.cid }
    }
    if (menuIdentity == null || playback?.info?.let { it.bvid to it.cid } != menuIdentity) {
        LaunchedEffect(menuIdentity, playbackUiState) { viewModel.hideDanmakuMenu() }
        return
    }
    val menuDmid = danmakuMenuState.dmid
    val menuUserHash = danmakuMenuState.userHash
    val reportWithDetails: (Int, String, Boolean) -> Unit = { reason, explanation, blockUser ->
        val current = viewModel.uiState.value as? app.bilipai.feature.video.viewmodel.VideoPlaybackUiState.Success
        if (current?.info?.let { it.bvid to it.cid } == menuIdentity) {
            sortPreferenceScope.launch {
                app.bilipai.feature.video.ui.components.submitPiliDanmakuReport(
                    context = context,
                    cid = menuIdentity.second,
                    dmid = menuDmid,
                    reason = reason,
                    explanation = explanation,
                    userHash = menuUserHash,
                    blockUser = blockUser,
                    settingsScope = activeDanmakuScope,
                )
            }
        } else {
            viewModel.toast("视频已切换，请重新选择弹幕")
        }
    }

    DanmakuContextMenu(
        text = danmakuMenuState.text,
        tapAnchor = danmakuMenuState.tapAnchor,
        onExpandActions = viewModel::expandDanmakuMenu,
        onDismiss = { viewModel.hideDanmakuMenu() },
        onLike = { viewModel.likeDanmaku(danmakuMenuState.dmid) },
        onRecall = { viewModel.recallDanmaku(danmakuMenuState.dmid) },
        onReport = { reason -> reportWithDetails(reason, "", false) },
        onReportWithDetails = reportWithDetails,
        durationMs = viewModel.getPlayerDuration().takeIf { it > 0L }
            ?: playback?.videoDurationMs?.coerceAtLeast(0L) ?: 0L,
        voteCount = danmakuMenuState.voteCount,
        hasLiked = danmakuMenuState.hasLiked,
        voteLoading = danmakuMenuState.voteLoading,
        canVote = danmakuMenuState.canVote,
        timestampJumpMs = resolveDanmakuTimestampJumpMs(danmakuMenuState.text),
        onSeekToTimestamp = { viewModel.seekTo(it) },
        canRecall = danmakuMenuState.isSelf,
        canBlockKeyword = danmakuMenuState.text.isNotBlank(),
        onBlockKeyword = {
            val updatedRules = appendDanmakuKeywordBlockRule(
                rawRules = activeDanmakuBlockRulesRaw,
                keyword = danmakuMenuState.text
            )
            val changed = updatedRules != activeDanmakuBlockRulesRaw
            sortPreferenceScope.launch {
                app.bilipai.core.store.SettingsManager.setDanmakuBlockRulesRaw(
                    context,
                    updatedRules,
                    activeDanmakuScope
                )
            }
            viewModel.toast(
                resolveDanmakuBlockActionFeedbackMessage(
                    target = DanmakuBlockActionTarget.KEYWORD,
                    changed = changed
                )
            )
        },
        canBlockUser = danmakuMenuState.userHash.isNotBlank(),
        onBlockUser = {
            sortPreferenceScope.launch {
                app.bilipai.feature.video.ui.components.blockPiliDanmakuSender(
                    context = context,
                    userHash = menuUserHash,
                    settingsScope = activeDanmakuScope,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExternalPlaylistQueueSheet(
    visible: Boolean,
    title: String,
    playlist: List<PlaylistItem>,
    currentIndex: Int,
    hazeState: HazeState,
    presentation: ExternalPlaylistQueueSheetPresentation,
    onDismiss: () -> Unit,
    onVideoSelected: (Int, PlaylistItem) -> Unit
) {
    // Keep the inline host composed while hidden so its exit can finish and a
    // quick reopen reverses the current transition instead of remounting it.
    if (!visible && presentation == ExternalPlaylistQueueSheetPresentation.MODAL) return

    app.bilipai.core.ui.LocalNavigationBackHandler(enabled = visible) {
        onDismiss()
    }

    val configuration = LocalConfiguration.current
    val listMaxHeight = resolveExternalPlaylistQueueListMaxHeightDp(configuration.screenHeightDp).dp
    val navigationBarBottomPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    val bottomSpacerHeight = resolveExternalPlaylistQueueBottomSpacerDp(
        navigationBarBottomPadding.value.roundToInt()
    ).dp
    val sheetShape = AppShapes.container(ContainerLevel.Sheet)
    val motionEnabled = LocalComponentMotionEnabled.current

    when (presentation) {
        ExternalPlaylistQueueSheetPresentation.INLINE_HAZE -> {
            val interactionSource = remember { MutableInteractionSource() }
            val useHazeEffect = shouldAllowRuntimeShaderBackedHazeEffect(Build.VERSION.SDK_INT)
            AnimatedVisibility(
                visible = visible,
                enter = EnterTransition.None,
                exit = ExitTransition.None,
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateEnterExit(
                                enter = if (motionEnabled) fadeIn(AppMotionTokens.standardSpec()) else EnterTransition.None,
                                exit = if (motionEnabled) fadeOut(AppMotionTokens.standardSpec()) else ExitTransition.None,
                            )
                            .background(Color.Black.copy(alpha = 0.18f))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onDismiss() }
                    )

                    AppSurface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .animateEnterExit(
                                enter = if (motionEnabled) {
                                    slideInVertically(AppMotionTokens.emphasizedSpec()) { it }
                                } else EnterTransition.None,
                                exit = if (motionEnabled) {
                                    slideOutVertically(AppMotionTokens.standardSpec()) { it }
                                } else ExitTransition.None,
                            )
                            .fillMaxWidth()
                            .clip(sheetShape)
                            .then(
                                if (useHazeEffect) {
                                    Modifier.hazeEffectCompat(
                                        state = hazeState,
                                        style = HazeMaterials.ultraThin()
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        shape = sheetShape,
                        color = AppSurfaceTokens.surface().copy(alpha = 0.74f),
                        tonalElevation = 0.dp,
                        border = BorderStroke(
                            width = 0.6.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        ExternalPlaylistQueueSheetContent(
                            title = title,
                            playlist = playlist,
                            currentIndex = currentIndex,
                            listMaxHeight = listMaxHeight,
                            bottomSpacerHeight = bottomSpacerHeight,
                            onVideoSelected = onVideoSelected,
                            onDismiss = onDismiss,
                        )
                    }
                }
            }
        }
        ExternalPlaylistQueueSheetPresentation.MODAL -> {
            AppModalBottomSheet(
                onDismissRequest = onDismiss,
                containerColor = Color.Transparent,
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
            ) {
                AppSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(sheetShape)
                        .unifiedBlur(hazeState = hazeState, shape = sheetShape),
                    shape = sheetShape,
                    color = AppSurfaceTokens.surface().copy(alpha = 0.80f),
                    tonalElevation = 0.dp,
                    border = BorderStroke(
                        width = 0.6.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                    )
                ) {
                    ExternalPlaylistQueueSheetContent(
                        title = title,
                        playlist = playlist,
                        currentIndex = currentIndex,
                        listMaxHeight = listMaxHeight,
                        bottomSpacerHeight = bottomSpacerHeight,
                        onVideoSelected = onVideoSelected,
                        onDismiss = onDismiss,
                    )
                }
            }
        }
    }
}

@Composable
internal fun ExternalPlaylistQueueSheetContent(
    title: String,
    playlist: List<PlaylistItem>,
    currentIndex: Int,
    listMaxHeight: androidx.compose.ui.unit.Dp,
    bottomSpacerHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    showDismissButton: Boolean = true,
    onVideoSelected: (Int, PlaylistItem) -> Unit,
    onDismiss: () -> Unit,
) {
    val selectionHaptic = rememberHapticFeedback(app.bilipai.core.ui.AppHapticScene.MENUS)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentIndex.coerceIn(0, playlist.lastIndex.coerceAtLeast(0)),
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            AppText(
                text = "${playlist.size}个视频",
                color = AppContentColors.accent(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (showDismissButton) {
                AppIconButton(onClick = onDismiss) {
                    AppIcon(
                        imageVector = rememberAppChevronDownIcon(),
                        contentDescription = "收起${title}队列",
                    )
                }
            }
        }

        AppHorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = listMaxHeight),
            contentPadding = PaddingValues(bottom = bottomSpacerHeight)
        ) {
            items(
                playlist.size,
                key = { index ->
                    val item = playlist[index]
                    resolveIndexedVideoLazyKey(
                        namespace = "video_playlist",
                        index = index,
                        bvid = item.bvid
                    )
                }
            ) { index ->
                val item = playlist[index]
                val selected = index == currentIndex
                val normalizedCoverUrl = normalizePlaylistCoverUrlForUi(item.cover)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (selected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable {
                            if (!selected) selectionHaptic(HapticType.LIGHT)
                            onVideoSelected(index, item)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = "${index + 1}",
                        color = if (selected) {
                            AppContentColors.accent()
                        } else {
                            AppContentColors.secondaryText()
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .width(96.dp)
                            .height(54.dp)
                            .clip(AppShapes.container(ContainerLevel.Field))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    ) {
                        if (normalizedCoverUrl.isNotEmpty()) {
                            AsyncImage(
                                model = normalizedCoverUrl,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                AppText(
                                    text = "无封面",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppContentColors.secondaryText()
                                )
                            }
                        }
                        if (item.duration > 0L) {
                            AppText(
                                text = FormatUtils.formatDuration(item.duration.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(3.dp)
                                    .background(Color.Black.copy(alpha = 0.68f), AppShapes.container(ContainerLevel.Chip))
                                    .padding(horizontal = 4.dp, vertical = 1.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        AppText(
                            text = item.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = AppContentColors.primaryText()
                        )
                        Spacer(Modifier.height(4.dp))
                        AppText(
                            text = listOfNotNull(
                                item.owner.takeIf { it.isNotBlank() },
                                item.publishedAt?.takeIf { it > 0L }?.let { FormatUtils.formatPublishTime(it) },
                            ).joinToString(" · "),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = AppContentColors.secondaryText(),
                            style = MaterialTheme.typography.bodySmall
                        )
                        val stats = listOfNotNull(
                            item.playCount?.let { "${FormatUtils.formatStat(it)}播放" },
                            item.commentCount?.let { "${FormatUtils.formatStat(it)}评论" },
                            "正在播放".takeIf { selected },
                        ).joinToString(" · ")
                        if (stats.isNotBlank()) {
                            AppText(
                                text = stats,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) AppContentColors.accent() else AppContentColors.secondaryText(),
                            )
                        }
                    }
                    if (selected) {
                        AppIcon(
                            imageVector = rememberAppPlayIcon(),
                            contentDescription = "当前播放",
                            tint = AppContentColors.accent()
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DetachedVideoCommentThreadHost(
    visible: Boolean,
    /** 横屏期间被隐藏过：恢复时跳过进场动画，直接显示原样。 */
    instantRestore: Boolean = false,
    successState: VideoPlaybackUiState.Success?,
    commentState: CommentUiState,
    commentViewModel: VideoCommentViewModel,
    forceInitialize: Boolean,
    viewModel: VideoPlaybackViewModel,
    onUpClick: (Long) -> Unit,
    onNavigateToRelatedVideo: (String) -> Unit,
    onSearchKeywordClick: (String) -> Unit,
    onOpenBilibiliLink: ((String) -> Unit)?,
    screenHeightPx: Int,
    topReservedPx: Int,
    onTimestampClick: (Long) -> Unit,
    onBackToTop: () -> Unit = {},
    onCoveredBlurProgressChange: ((Float) -> Unit)? = null,
    threadListModifier: Modifier = Modifier,
) {
    if (!visible) return

    val subReplyState by commentViewModel.subReplyState.collectAsStateWithLifecycle()

    VideoCommentSheetHost(
        instantRestore = instantRestore,
        threadListModifier = threadListModifier,
        mainSheetVisible = resolveVideoDetailCommentThreadHostMainSheetVisible(
            useEmbeddedPresentation = app.bilipai.feature.video.ui.pager
                .shouldUseEmbeddedVideoSubReplyPresentation(),
            subReplyVisible = subReplyState.visible
        ),
        onDismiss = { commentViewModel.closeSubReply() },
        commentViewModel = commentViewModel,
        aid = successState?.info?.aid ?: 0L,
        upMid = commentState.upMid,
        expectedReplyCount = commentState.replyCount,
        emoteMap = successState?.emoteMap ?: emptyMap(),
        onRootCommentClick = { viewModel.openRootCommentComposer() },
        onReplyClick = { replyItem ->
            android.util.Log.d("VideoDetailScreen", "📝 Reply to: ${replyItem.member.uname}")
            viewModel.setReplyingTo(replyItem)
            viewModel.showCommentInputDialog()
        },
        onUserClick = onUpClick,
        onVideoClick = onNavigateToRelatedVideo,
        onSearchKeywordClick = onSearchKeywordClick,
        onOpenBilibiliLink = onOpenBilibiliLink,
        screenHeightPx = screenHeightPx,
        topReservedPx = topReservedPx,
        onTimestampClick = onTimestampClick,
        maxTimestampMs = successState?.videoDurationMs?.takeIf { it > 0L },
        onBackToTop = onBackToTop,
        forceInitialize = forceInitialize,
        handleFraudEvents = false,
        // 楼中楼（嵌入呈现）不盖全屏阴影：播放器上方保持可见，点背景关闭仍有效。
        maxScrimAlphaOverride = 0f,
        onCoveredBlurProgressChange = onCoveredBlurProgressChange
    )
}
