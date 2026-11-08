package app.bilipai.feature.video.screen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.bilipai.core.store.TokenManager
import app.bilipai.feature.video.viewmodel.FavoriteFolderSaveEvent
import app.bilipai.feature.video.viewmodel.VideoComposerViewModel
import app.bilipai.feature.video.viewmodel.VideoEngagementEvent
import app.bilipai.feature.video.viewmodel.VideoEngagementViewModel
import app.bilipai.feature.video.viewmodel.VideoPlaybackUiState
import app.bilipai.feature.video.viewmodel.VideoPlaybackViewModel
import app.bilipai.feature.video.viewmodel.VideoSubjectSnapshot
import app.bilipai.feature.video.viewmodel.VideoSupplementViewModel
import app.bilipai.feature.video.viewmodel.toEngagementSeed
import app.bilipai.feature.video.viewmodel.toSupplementSeed

/** Keeps cross-domain ViewModel wiring out of the screen renderer. */
@Composable
internal fun VideoDetailDomainEffects(
    context: Context,
    isVisible: Boolean,
    uiState: VideoPlaybackUiState,
    watchLaterSourceBvid: String? = null,
    subjectSnapshotState: State<VideoSubjectSnapshot?>,
    favoriteFolderSaveEventState: State<FavoriteFolderSaveEvent?>,
    playbackViewModel: VideoPlaybackViewModel,
    engagementViewModel: VideoEngagementViewModel,
    composerViewModel: VideoComposerViewModel,
    supplementViewModel: VideoSupplementViewModel,
) {
    val subjectSnapshot by subjectSnapshotState
    val favoriteFolderSaveEvent by favoriteFolderSaveEventState
    LaunchedEffect(context) {
        engagementViewModel.initWithContext(context)
    }
    val coinSuccessHaptic = androidx.compose.runtime.rememberUpdatedState(
        app.bilipai.feature.video.ui.components.rememberVideoActionSuccessHaptic(isVisible)
    )
    LaunchedEffect(engagementViewModel) {
        engagementViewModel.events.collect { event ->
            when (event) {
                is VideoEngagementEvent.Message -> playbackViewModel.toast(event.text)
                is VideoEngagementEvent.CoinSucceeded -> {
                    if (subjectSnapshotState.value == event.subject &&
                        app.bilipai.feature.video.viewmodel.matchesVideoEngagementAccount(event.accountSession)
                    ) coinSuccessHaptic.value()
                }
                is VideoEngagementEvent.OpenFollowGroups ->
                    playbackViewModel.showFollowGroupDialogForUser(event.mid)
                is VideoEngagementEvent.LoadVideo ->
                    playbackViewModel.loadVideo(event.bvid, autoPlay = true)
                VideoEngagementEvent.InvalidateFavoriteFolders ->
                    playbackViewModel.invalidateFavoriteFolderCache()
            }
        }
    }

    // 来源列表已确认的成员身份只用于该内容与进入时的账号，不能沿用到切集或换号。
    val watchLaterSourceAccount = remember(watchLaterSourceBvid) { TokenManager.accountIdentity.value }
    val engagementSeed = (uiState as? VideoPlaybackUiState.Success)?.let { success ->
        success.toEngagementSeed().let { seed ->
            if (success.info.bvid == watchLaterSourceBvid &&
                TokenManager.accountIdentity.value == watchLaterSourceAccount
            ) seed.copy(isInWatchLater = true) else seed
        }
    }
    val supplementSeed = (uiState as? VideoPlaybackUiState.Success)?.toSupplementSeed()
    LaunchedEffect(subjectSnapshot, engagementSeed) {
        val subject = subjectSnapshot ?: return@LaunchedEffect
        val seed = engagementSeed ?: return@LaunchedEffect
        engagementViewModel.bindSubject(subject, seed)
        composerViewModel.bindSubject(subject)
    }
    LaunchedEffect(subjectSnapshot, supplementSeed) {
        val subject = subjectSnapshot ?: return@LaunchedEffect
        val seed = supplementSeed ?: return@LaunchedEffect
        supplementViewModel.bindSubject(subject, seed)
    }
    LaunchedEffect(favoriteFolderSaveEvent?.version) {
        favoriteFolderSaveEvent?.let { event ->
            if (subjectSnapshot?.aid == event.aid) {
                engagementViewModel.applyFavoriteFolderResult(event.isFavorited)
            }
        }
    }
    LaunchedEffect(isVisible) {
        supplementViewModel.setVisible(isVisible)
    }
}
