package app.bilipai.feature.home.components.cards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.layer.GraphicsLayer
import app.bilipai.core.ui.transition.LocalVideoCardTransitionBackgroundState
import app.bilipai.core.ui.transition.VideoCardTransitionBackgroundState
import app.bilipai.core.ui.transition.captureNativeCoverOverlayLayer
import app.bilipai.core.ui.transition.captureNativeVideoCardImage
import app.bilipai.core.ui.transition.captureWholeCardReturnBitmap
import app.bilipai.core.ui.transition.captureVideoCardPixelsAfterEntry
import app.bilipai.core.ui.transition.rememberNativeVideoCardLayer
import app.bilipai.core.ui.transition.rememberVideoCardPixelCaptureAllowed
import app.bilipai.core.util.CardPositionManager
import app.bilipai.core.util.VideoCardPixelOwner
import kotlinx.coroutines.CompletableDeferred

/** Mounted only while a card prepares its source. No scope or job outlives this composition. */
internal class VideoCardCaptureState(
    val cardLayer: GraphicsLayer,
    val coverOverlayLayer: GraphicsLayer,
    private val captureAllowed: State<Boolean>,
    private val background: State<VideoCardTransitionBackgroundState>,
) {
    private val frozen = mutableStateOf(false)
    // Draw completion wakes the click coroutine without writing snapshot state from draw.
    private val cardRecorded = CompletableDeferred<Unit>()
    private val coverRecorded = CompletableDeferred<Unit>()

    fun onCardRecorded() { cardRecorded.complete(Unit) }
    fun onCoverRecorded() { coverRecorded.complete(Unit) }

    suspend fun awaitSourceRecorded() {
        cardRecorded.await()
        coverRecorded.await()
    }

    fun freezeOnClick(): VideoCardPixelOwner? {
        frozen.value = true
        captureNativeVideoCardImage(cardLayer)
        captureNativeCoverOverlayLayer(coverOverlayLayer)
        return CardPositionManager.videoCardPixelOwnerFor(CardPositionManager.lastClickedVideoSourceKey)
    }

    fun freezeCard(): Boolean = frozen.value && CardPositionManager.isNativeVideoCardLayerCurrentOwner(cardLayer)
    fun freezeCover(): Boolean = frozen.value && CardPositionManager.isNativeCoverOverlayLayerCurrentOwner(coverOverlayLayer)

    suspend fun captureAfterEntry(owner: VideoCardPixelOwner) {
        captureVideoCardPixelsAfterEntry(
            phaseProvider = { background.value.phaseProvider() },
            isReturnGestureInProgress = {
                background.value.isReturnGestureInProgressProvider() || background.value.isGestureRestoreInProgressProvider()
            },
            ownsCapture = {
                !cardLayer.isReleased && CardPositionManager.isActiveVideoCardPixelOwner(owner) &&
                    CardPositionManager.isNativeVideoCardLayerCurrentOwner(cardLayer)
            },
            captureAllowed = { captureAllowed.value },
            entryWorkReleased = { owner.entryWorkReleased },
            capture = { captureWholeCardReturnBitmap(cardLayer, owner) },
        )
    }
}

@Composable
internal fun rememberVideoCardCaptureState(bvid: String): VideoCardCaptureState {
    val cardLayer = rememberNativeVideoCardLayer()
    val coverLayer = rememberNativeVideoCardLayer()
    val allowed = rememberVideoCardPixelCaptureAllowed()
    val background = rememberUpdatedState(LocalVideoCardTransitionBackgroundState.current)
    return remember(bvid, cardLayer, coverLayer, allowed, background) {
        VideoCardCaptureState(cardLayer, coverLayer, allowed, background)
    }
}
