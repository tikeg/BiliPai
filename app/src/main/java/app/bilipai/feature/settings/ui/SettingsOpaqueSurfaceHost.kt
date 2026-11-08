package app.bilipai.feature.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import app.bilipai.core.ui.globalWallpaperAwareBackground
import app.bilipai.core.ui.wallpaper.LocalGlobalWallpaperBackdropSource
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.LocalGlobalWallpaperBackdropVisible

/**
 * Settings retain an opaque visual backing. Static global wallpaper replays the root
 * recording rather than accumulating translucent page fills during push/pop.
 */
@Composable
internal fun SettingsOpaqueSurfaceHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val sharedWallpaper = LocalGlobalWallpaperBackdropVisible.current &&
        LocalGlobalWallpaperBackdropSource.current != null
    CompositionLocalProvider(LocalGlobalWallpaperBackdropVisible provides sharedWallpaper) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .globalWallpaperAwareBackground(AppSurfaceTokens.groupedListContainer()),
        ) {
            content()
        }
    }
}
