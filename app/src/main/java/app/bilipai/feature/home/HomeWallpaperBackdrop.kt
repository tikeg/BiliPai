package app.bilipai.feature.home

import app.bilipai.core.ui.LocalGlobalWallpaperBackdropVisible
import app.bilipai.core.ui.wallpaper.LocalGlobalWallpaperUri
import app.bilipai.core.ui.wallpaper.LocalGlobalWallpaperBackdropSource
import app.bilipai.core.ui.wallpaper.sharedGlobalWallpaperBackground
import app.bilipai.core.ui.wallpaper.LocalHomeWallpaperSelection
import app.bilipai.core.ui.wallpaper.WallpaperTransform
import app.bilipai.core.ui.wallpaper.WallpaperMediaLoadState
import app.bilipai.core.util.LocalWindowSizeClass
import androidx.compose.ui.unit.IntSize
import coil3.request.crossfade

import app.bilipai.core.ui.MediaContrastPalette

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import app.bilipai.core.ui.wallpaper.WallpaperReadabilityProfile
import app.bilipai.core.ui.wallpaper.resolveWallpaperBodyTintAlpha
import app.bilipai.core.ui.wallpaper.resolveWallpaperProtectionAlpha
import app.bilipai.feature.home.components.cards.LocalWallpaperPalette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Scale
import app.bilipai.core.ui.adaptive.MotionTier
import app.bilipai.core.ui.motion.rememberSystemReduceMotion
import app.bilipai.core.ui.transition.VideoCardTransitionBackgroundPhase
import app.bilipai.core.ui.transition.resolveVideoCardTransitionExposure
import app.bilipai.core.ui.transition.videoCardTransitionBackgroundEffect
import app.bilipai.core.ui.wallpaper.isVideoWallpaper

/**
 * App 根层全局壁纸。
 * 景深值直接延迟读取根层单时钟，不再经每帧 Snapshot 状态桥接。
 */
@Composable
internal fun DepthSyncedGlobalHomeWallpaperBackdrop(
    wallpaperUri: String,
    appearance: HomeWallpaperBackdropAppearance,
    baseColor: Color,
    depthProgressProvider: () -> Float,
    depthPhaseProvider: () -> VideoCardTransitionBackgroundPhase,
    depthGestureRestoreProvider: () -> Boolean,
    sourceBoundsProvider: () -> Rect? = { null },
    isDataSaverActive: Boolean = false,
    isLightBackground: Boolean = false,
    realtimeBlurEnabled: Boolean = true,
    playbackEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val phase = depthPhaseProvider()
    val applyDepth = shouldApplyVideoCardDepthToGlobalHomeWallpaper(
        wallpaperVisible = appearance.visible,
        phase = phase,
    )
    val motionTier =
        if (rememberSystemReduceMotion()) MotionTier.Reduced else MotionTier.Normal
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (applyDepth) {
                    Modifier.videoCardTransitionBackgroundEffect(
                        progressProvider = depthProgressProvider,
                        phaseProvider = depthPhaseProvider,
                        exposureProvider = {
                            resolveVideoCardTransitionExposure(
                                phase = depthPhaseProvider(),
                                predictiveBackInProgress = false,
                                gestureRestoreInProgress = depthGestureRestoreProvider(),
                            )
                        },
                        isGestureRestoreInProgressProvider = depthGestureRestoreProvider,
                        motionTierProvider = { motionTier },
                        isLightBackgroundProvider = { isLightBackground },
                        realtimeBlurEnabledProvider = { realtimeBlurEnabled },
                        sourceBoundsProvider = sourceBoundsProvider,
                    )
                } else {
                    Modifier
                }
            )
    ) {
        HomeWallpaperBackdrop(
            wallpaperUri = wallpaperUri,
            appearance = appearance,
            baseColor = baseColor,
            isDataSaverActive = isDataSaverActive,
            playbackEnabled = playbackEnabled,
            reuseGlobalSource = false,
            playbackPriority = -10,
        )
    }
}

@Composable
internal fun HomeWallpaperBackdrop(
    wallpaperUri: String,
    appearance: HomeWallpaperBackdropAppearance,
    baseColor: Color,
    isDataSaverActive: Boolean = false,
    playbackEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    reuseGlobalSource: Boolean = true,
    transformProvider: (() -> WallpaperTransform)? = null,
    playbackPriority: Int = 0,
    onMediaLoadState: (WallpaperMediaLoadState) -> Unit = {},
    onMediaSourceSize: (IntSize) -> Unit = {},
    readabilityOverride: WallpaperReadabilityProfile? = null,
    localProtectionScale: Float = 1f,
) {
    // Static GLOBAL wallpapers are decoded and treated once by the app root. Replaying the
    // same recording also aligns home/card sources with every other destination.
    if (reuseGlobalSource && appearance.visible &&
        LocalGlobalWallpaperBackdropVisible.current &&
        LocalGlobalWallpaperBackdropSource.current != null &&
        wallpaperUri == LocalGlobalWallpaperUri.current
    ) {
        Box(modifier.fillMaxSize().sharedGlobalWallpaperBackground(baseColor))
        return
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
    ) {
        if (!appearance.visible) return@Box

        val selection = LocalHomeWallpaperSelection.current
        val isTablet = LocalWindowSizeClass.current.isTablet
        val usesHomeSelection = selection.uri == wallpaperUri ||
            (selection.uri.isBlank() && wallpaperUri == LocalGlobalWallpaperUri.current)
        val savedTransform = if (usesHomeSelection) {
            if (isTablet) selection.tabletTransform else selection.mobileTransform
        } else WallpaperTransform()
        val mediaTransform = transformProvider ?: { savedTransform }
        val readability = readabilityOverride ?: LocalWallpaperPalette.current?.readability
            ?.takeIf { usesHomeSelection && transformProvider == null }
        val bodyAlpha = if (readability != null) resolveWallpaperBodyTintAlpha(appearance.baseBackgroundAlpha)
            else appearance.baseBackgroundAlpha
        val scrimAlpha = if (readability != null) appearance.scrimAlpha * 0.35f else appearance.scrimAlpha
        val bottomAlpha = readability?.bottom?.let {
            resolveWallpaperProtectionAlpha(it, baseColor, MaterialTheme.colorScheme.onSurface, appearance.blurRadiusDp > 0f)
        } ?: appearance.bottomScrimAlpha
        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val decodeSize = remember(
            configuration.screenWidthDp,
            configuration.screenHeightDp,
            density.density,
            isDataSaverActive,
            appearance.blurRadiusDp
        ) {
            resolveHomeWallpaperDecodeSizePx(
                screenWidthDp = configuration.screenWidthDp,
                screenHeightDp = configuration.screenHeightDp,
                density = density.density,
                isDataSaverActive = isDataSaverActive,
                blurRadiusDp = appearance.blurRadiusDp
            )
        }
        val imageRequest = remember(context, wallpaperUri, decodeSize) {
            val cacheKey = "home_wallpaper_${wallpaperUri.hashCode()}_${decodeSize.first}x${decodeSize.second}"
            ImageRequest.Builder(context)
                .data(wallpaperUri)
                .size(decodeSize.first, decodeSize.second)
                .scale(Scale.FILL)
                .memoryCacheKey(cacheKey)
                .diskCacheKey(cacheKey)
                .crossfade(180)
                .build()
        }
        val imageModifier = Modifier
            .fillMaxSize()
            .then(
                if (appearance.blurRadiusDp > 0f) {
                    Modifier.blur(appearance.blurRadiusDp.dp)
                } else {
                    Modifier
                }
            )

        app.bilipai.core.ui.wallpaper.WallpaperMedia(
            uri = wallpaperUri,
            imageModel = imageRequest,
            playbackEnabled = playbackEnabled && !isDataSaverActive,
            transformProvider = mediaTransform,
            playbackPriority = playbackPriority,
            onLoadState = onMediaLoadState,
            onSourceSize = onMediaSourceSize,
            modifier = imageModifier
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(baseColor.copy(alpha = bodyAlpha))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MediaContrastPalette.Scrim.copy(alpha = scrimAlpha))
        )
        Box(
            modifier = Modifier
                .then(
                    if (readability != null) Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height((160f * localProtectionScale.coerceIn(0.05f, 1f)).dp)
                    else Modifier.fillMaxSize()
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            baseColor.copy(alpha = bottomAlpha)
                        )
                    )
                )
        )
    }
}

/**
 * The first wallpaper glass path only samples stable image content. Animated media keeps the
 * existing lightweight tint until a frame-aware source is available.
 */
internal fun isStaticHomeWallpaperUri(uri: String): Boolean {
    val extension = uri
        .substringBefore('?')
        .substringBefore('#')
        .substringAfterLast('.', missingDelimiterValue = "")
        .lowercase()
    return !isVideoWallpaper(uri) && extension !in setOf("gif", "webp")
}
