package app.bilipai.core.ui.wallpaper

import app.bilipai.core.ui.adaptive.toAdaptiveWidthClass
import app.bilipai.core.ui.wallpaper.resolveProfileWallpaperLayout as resolveSharedProfileWallpaperLayout
import app.bilipai.core.ui.wallpaper.resolveSplashWallpaperLayout as resolveSharedSplashWallpaperLayout
import app.bilipai.core.util.WindowWidthSizeClass

fun resolveSplashWallpaperLayout(
    widthSizeClass: WindowWidthSizeClass,
    imageAspectRatio: Float? = null,
    screenAspectRatio: Float = 9f / 16f,
    compactAspectMismatchThreshold: Float = 0.08f,
): SplashWallpaperLayout = resolveSharedSplashWallpaperLayout(
    widthClass = widthSizeClass.toAdaptiveWidthClass(),
    imageAspectRatio = imageAspectRatio,
    screenAspectRatio = screenAspectRatio,
    compactAspectMismatchThreshold = compactAspectMismatchThreshold,
)

fun resolveProfileWallpaperLayout(
    widthSizeClass: WindowWidthSizeClass,
): ProfileWallpaperLayout = resolveSharedProfileWallpaperLayout(
    widthClass = widthSizeClass.toAdaptiveWidthClass(),
)
