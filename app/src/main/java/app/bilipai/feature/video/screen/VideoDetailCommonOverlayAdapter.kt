package app.bilipai.feature.video.screen

import androidx.compose.runtime.Composable
import androidx.media3.common.Player
import app.bilipai.feature.video.interaction.InteractiveChoicePanelUiState
import app.bilipai.feature.video.player.PlaylistItem
import app.bilipai.feature.video.ui.components.CoinDialog
import app.bilipai.feature.video.ui.components.InteractiveChoiceOverlay
import app.bilipai.feature.video.viewmodel.VideoEngagementUiState
import app.bilipai.feature.video.viewmodel.VideoEngagementViewModel
import app.bilipai.feature.video.viewmodel.VideoPlaybackViewModel
import dev.chrisbanes.haze.HazeState

@Composable
internal fun VideoDetailCommonOverlayAdapter(
    interactiveChoicePanel: InteractiveChoicePanelUiState,
    engagementState: VideoEngagementUiState,
    playbackViewModel: VideoPlaybackViewModel,
    engagementViewModel: VideoEngagementViewModel,
    queueVisible: Boolean,
    queueTitle: String,
    playlist: List<PlaylistItem>,
    playlistCurrentIndex: Int,
    hazeState: HazeState,
    queuePresentation: ExternalPlaylistQueueSheetPresentation,
    player: Player,
    onDismissQueue: () -> Unit,
    onVideoSelected: (Int, PlaylistItem) -> Unit,
) {
    InteractiveChoiceOverlay(
        state = interactiveChoicePanel,
        onSelectChoice = { edgeId, targetCid ->
            playbackViewModel.selectInteractiveChoice(edgeId = edgeId, cid = targetCid)
        },
        onDismiss = playbackViewModel::dismissInteractiveChoicePanel,
    )
    CoinDialog(
        visible = engagementState.coinDialogVisible,
        contentKey = engagementState.subject to engagementState.accountSession,
        currentCoinCount = engagementState.coinCount,
        userBalance = engagementState.userCoinBalance,
        maxCoins = engagementState.coinLimit,
        onDismiss = { engagementViewModel.setCoinDialogVisible(false) },
        onConfirm = engagementViewModel::doCoin,
    )
    VideoDetailFollowGroupDialog(viewModel = playbackViewModel)
    ExternalPlaylistQueueSheet(
        visible = queueVisible,
        title = queueTitle,
        playlist = playlist,
        currentIndex = playlistCurrentIndex,
        hazeState = hazeState,
        presentation = queuePresentation,
        onDismiss = onDismissQueue,
        onVideoSelected = onVideoSelected,
    )
    VideoDetailPlaybackEndedDialog(viewModel = playbackViewModel, player = player)
}
