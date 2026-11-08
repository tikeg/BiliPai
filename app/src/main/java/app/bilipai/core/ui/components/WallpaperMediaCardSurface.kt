package app.bilipai.core.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.bilipai.core.ui.LocalGlobalWallpaperBackdropVisible
import app.bilipai.core.ui.isAppInDarkTheme
import app.bilipai.core.ui.performance.isLowBlurBudgetForced
import app.bilipai.core.ui.wallpaper.LocalGlobalWallpaperBackdropSource
import app.bilipai.core.ui.wallpaper.sharedGlobalWallpaperBackground
import app.bilipai.feature.home.components.cards.LocalHomeCardFrostedGlassEnabled
import top.yukonga.miuix.kmp.blur.blur
import top.yukonga.miuix.kmp.blur.drawBackdrop

internal data class WallpaperMediaCardSurface(
    val modifier: Modifier,
    val containerColor: Color,
)

/** Shared media-card treatment; controls, badges and page surfaces opt out by default. */
@Composable
internal fun wallpaperMediaCardSurface(
    containerColor: Color,
    shape: Shape,
    enabled: Boolean = true,
): WallpaperMediaCardSurface {
    val source = LocalGlobalWallpaperBackdropSource.current
    // The card-background switch is independent of liquid chrome. When disabled, or outside
    // GLOBAL wallpaper scope, preserve every native color, shape and modifier unchanged.
    val active = enabled && LocalHomeCardFrostedGlassEnabled.current &&
        LocalGlobalWallpaperBackdropVisible.current && source?.isReady == true &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isLowBlurBudgetForced()
    if (!active || source == null) return WallpaperMediaCardSurface(Modifier, containerColor)

    val radiusPx = with(LocalDensity.current) { 24.dp.toPx() }
    return WallpaperMediaCardSurface(
        modifier = Modifier
            .clip(shape)
            // Include opaque wallpaper pixels in native card snapshots before transitions.
            // Sample only the independent wallpaper layer, never covers, labels or the list.
            .sharedGlobalWallpaperBackground(containerColor)
            .drawBackdrop(
                backdrop = source.backdrop,
                shape = { shape },
                effects = { blur(radiusPx, radiusPx) },
            ),
        // Match the neutral realtime frosted plate used by homepage recommendation cards.
        containerColor = containerColor.copy(alpha = if (isAppInDarkTheme()) 0.44f else 0.36f),
    )
}

@Composable
internal fun Modifier.wallpaperAwareMediaCardBackground(color: Color, shape: Shape): Modifier {
    val surface = wallpaperMediaCardSurface(color, shape)
    return this.then(surface.modifier).background(surface.containerColor)
}
