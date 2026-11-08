package app.bilipai.core.ui.wallpaper

import androidx.compose.runtime.compositionLocalOf
import app.bilipai.core.store.home.HomeWallpaperSelection

/** One atomic selection shared by the app root, home, chat and wallpaper surfaces. */
internal val LocalHomeWallpaperSelection = compositionLocalOf { HomeWallpaperSelection() }
