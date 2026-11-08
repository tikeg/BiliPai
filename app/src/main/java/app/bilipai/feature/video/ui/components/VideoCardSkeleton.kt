package app.bilipai.feature.video.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.bilipai.core.store.HomeWallpaperEffectMode
import app.bilipai.feature.home.HomeFeedSkeletonCard
import app.bilipai.feature.home.rememberHomeFeedSkeletonPulseState

/** BGM recommendations use ElegantVideoCard, so reuse its home-feed loading silhouette. */
@Composable
fun VideoCardSkeleton(
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") index: Int = 0,
    coverAspectRatio: Float = app.bilipai.core.ui.videoCardCoverAspectRatio(),
    compactMetadata: Boolean = false,
) {
    val pulse = rememberHomeFeedSkeletonPulseState()
    HomeFeedSkeletonCard(
        pulse = { pulse.value },
        wallpaperTintEnabled = false,
        wallpaperEffectMode = HomeWallpaperEffectMode.OFF,
        isDataSaverActive = true,
        coverAspectRatio = coverAspectRatio,
        modifier = modifier,
        compactMetadata = compactMetadata,
    )
}
