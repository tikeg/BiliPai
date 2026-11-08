package app.bilipai.feature.video.ui.overlay

import app.bilipai.core.util.rememberHapticFeedback
import app.bilipai.core.util.HapticType

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntRect
import app.bilipai.core.store.PlaybackCompletionBehavior
import app.bilipai.feature.video.ui.components.PlayerListPopupPlacement
import app.bilipai.feature.video.ui.components.PlayerSelectionPopup
import app.bilipai.feature.video.ui.components.PlayerSelectionItem
import app.bilipai.feature.video.ui.components.playerPopupSelectedOption

@Composable
internal fun PlaybackOrderSelectionSheet(
    currentBehavior: PlaybackCompletionBehavior,
    onSelect: (PlaybackCompletionBehavior) -> Unit,
    onDismiss: () -> Unit,
    isFullscreen: Boolean = false,
    placement: PlayerListPopupPlacement = if (isFullscreen) PlayerListPopupPlacement.END else PlayerListPopupPlacement.CENTER,
    anchorBounds: IntRect? = null,
) {
    val selectionHaptic = rememberHapticFeedback(app.bilipai.core.ui.AppHapticScene.MENUS)
    PlayerSelectionPopup(title = "选择播放顺序", showTitle = false,
        onDismissRequest = onDismiss, placement = placement, anchorBounds = anchorBounds) {
        playerPlaybackOrderOptions.forEachIndexed { index, behavior ->
            val selected = currentBehavior == behavior
            PlayerSelectionItem(text = behavior.label, selected = selected, enabled = !selected,
                index = index, optionCount = playerPlaybackOrderOptions.size,
                modifier = Modifier.playerPopupSelectedOption(selected),
                onClick = {
                    if (!selected) selectionHaptic(HapticType.LIGHT)
                    onSelect(behavior)
                    onDismiss()
                })
        }
    }
}
