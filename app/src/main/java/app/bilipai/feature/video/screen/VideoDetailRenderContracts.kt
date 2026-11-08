package app.bilipai.feature.video.screen

import androidx.compose.runtime.Immutable
import app.bilipai.data.model.response.FavFolder
import app.bilipai.data.model.response.ReplyItem
import app.bilipai.feature.video.note.VideoNoteEditorDocument
import app.bilipai.feature.video.viewmodel.SubReplySortMode
import app.bilipai.feature.video.viewmodel.CommentSortMode

@Immutable
internal data class VideoDetailPlaybackActions(
    val resolveNextWatch: () -> app.bilipai.feature.video.playback.next.NextWatchSuggestion? = { null },
    val playNextWatch: (app.bilipai.feature.video.playback.next.NextWatchSuggestion) -> Boolean = { false },
    val changeQuality: (Int) -> Unit,
    val reloadVideo: () -> Unit,
    val switchCdn: () -> Unit,
    val switchCdnTo: (Int) -> Unit,
    val probeCdnCandidates: () -> Unit,
    val setAudioMode: (Boolean) -> Unit,
    val setSleepTimer: (Int?) -> Unit,
    val switchPage: (Int) -> Unit,
    val jumpToLastPlayedPart: () -> Unit = {},
    val dismissPartResumeNotice: () -> Unit = {},
    val openDownloadDialog: () -> Unit,
    val showDanmakuSendDialog: () -> Unit,
    val skipSponsorSegment: () -> Unit,
    val dismissSponsorSkipButton: () -> Unit,
    val voteSponsorSegment: (Int) -> Unit,
    val markSponsorContributionBoundary: () -> Unit,
    val markWholeVideoAsSponsor: () -> Unit,
    val setSponsorContributionCategory: (String) -> Unit,
    val setSponsorContributionActionType: (String) -> Unit,
    val submitSponsorContribution: () -> Unit,
    val cancelSponsorContribution: () -> Unit,
    val notifyExplicitSeek: (Long) -> Unit,
    val setVideoCodec: (String) -> Unit,
    val setVideoSecondCodec: (String) -> Unit,
    val setAudioQuality: (Int) -> Unit,
    val applyPlaybackSpeed: (Float) -> Boolean,
    val changeAudioLanguage: (String?) -> Unit,
    val saveCover: () -> Unit,
    val downloadAudio: () -> Unit,
    val selectSubtitleTrack: (String) -> Unit,
    val showFavoriteFolderDialog: () -> Unit,
    val toggleFavoriteFolderSelection: (FavFolder) -> Unit,
    val saveFavoriteFolderSelection: () -> Unit,
    val dismissFavoriteFolderDialog: () -> Unit,
    val createFavoriteFolder: (String, String, Boolean) -> Unit,
    val retryAiSummary: () -> Unit,
    val createVideoNoteDraftFromAiSummary: () -> Unit,
    val openVideoNoteEditor: () -> Unit,
    val closeVideoNoteEditor: () -> Unit,
    val updateVideoNoteEditorDocument: (VideoNoteEditorDocument) -> Unit,
    val currentVideoNoteTimestamp: () -> app.bilipai.feature.video.note.VideoNoteBlock.Timestamp?,
    val seekTo: (Long) -> Unit,
    val saveVideoNote: (VideoNoteEditorDocument?) -> Unit,
    val deleteVideoNote: () -> Unit,
    val retryVideoNote: () -> Unit,
    val loadMorePublicVideoNotes: () -> Unit,
    val openRootCommentComposer: () -> Unit,
    val replyTo: (ReplyItem) -> Unit,
    val markVideoNotInterested: () -> Unit,
    val likeDanmaku: (Long) -> Unit = {},
    val likeDanmakuToggle: (dmid: Long, like: Boolean) -> Unit = { _, _ -> },
    val likedDanmakuIds: kotlinx.coroutines.flow.StateFlow<Set<Long>> =
        kotlinx.coroutines.flow.MutableStateFlow(emptySet()),
    val reportDanmaku: (dmid: Long, reason: Int) -> Unit = { _, _ -> },
    val recallDanmaku: (Long) -> Unit = {},
    val menuActions: app.bilipai.feature.video.ui.overlay.PiliPlayerMenuActions =
        app.bilipai.feature.video.ui.overlay.PiliPlayerMenuActions(),
    val retryPlayback: () -> Unit = reloadVideo
)

@Immutable
internal data class VideoDetailEngagementActions(
    val toggleFollow: () -> Unit,
    val followOwner: () -> Unit,
    val toggleFavorite: () -> Unit,
    val onFavoriteAction: (isLongPress: Boolean) -> Unit,
    val toggleLike: () -> Unit,
    val toggleDislike: () -> Unit = {},
    val openCoinDialog: () -> Unit,
    val doTripleAction: () -> Unit,
    val toggleWatchLater: () -> Unit
)

@Immutable
internal data class VideoDetailCommentActions(
    val loadComments: () -> Unit,
    val refreshComments: () -> Unit,
    val setSortMode: (CommentSortMode) -> Unit,
    val deleteComment: (Long) -> Unit,
    val startDissolve: (Long) -> Unit,
    val loadMoreSubReplies: () -> Unit,
    val refreshSubReplies: () -> Unit,
    val setSubReplySortMode: (SubReplySortMode) -> Unit,
    val openSubReply: (ReplyItem, Long) -> Unit,
    val openSubReplyConversation: (ReplyItem) -> Unit,
    val closeSubReplyConversation: () -> Unit,
    val closeSubReply: () -> Unit,
    val startSubDissolve: (Long) -> Unit,
    val deleteSubComment: (Long) -> Unit,
    val likeComment: (Long) -> Unit,
    val hateComment: (Long) -> Unit,
    val reportComment: (Long, Int) -> Unit,
    val toggleTopComment: (ReplyItem) -> Unit,
    val checkCommentFraud: (ReplyItem) -> Unit
)

@Immutable
internal data class VideoDetailNavigationActions(
    val back: () -> Unit,
    val home: () -> Unit,
    val toggleFullscreen: () -> Unit,
    val enterPortraitFullscreen: () -> Unit,
    val enterPip: () -> Unit,
    val navigateToAudioMode: () -> Unit
)
