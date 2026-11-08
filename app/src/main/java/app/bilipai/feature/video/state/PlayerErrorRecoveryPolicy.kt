package app.bilipai.feature.video.state

import androidx.media3.common.PlaybackException

internal typealias PlayerErrorRecoveryAction = app.bilipai.core.player.PlayerErrorRecoveryAction
internal fun decidePlayerErrorRecovery(errorCode: Int, hasCdnAlternatives: Boolean, retryCount: Int, maxRetries: Int,
    cdnSwitchCount: Int, maxCdnSwitches: Int, isDecoderLikeFailure: Boolean, isPremiumAudioFailure: Boolean = false) =
    app.bilipai.core.player.decidePlayerErrorRecovery(errorCode, hasCdnAlternatives, retryCount, maxRetries,
        cdnSwitchCount, maxCdnSwitches, isDecoderLikeFailure, isPremiumAudioFailure)
internal fun isNetworkPlaybackError(errorCode: Int) = app.bilipai.core.player.isNetworkPlaybackError(errorCode)
internal fun isDecoderLikeFailure(errorMessage: String?, causeClassName: String?) =
    app.bilipai.core.player.isDecoderLikeFailure(errorMessage, causeClassName)
