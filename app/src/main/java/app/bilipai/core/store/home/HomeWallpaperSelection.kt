package app.bilipai.core.store.home

import app.bilipai.core.ui.wallpaper.WallpaperTransform

/** URI and both device transforms are published as one persisted selection. */
data class HomeWallpaperSelection(
    val uri: String = "",
    val mobileTransform: WallpaperTransform = WallpaperTransform(),
    val tabletTransform: WallpaperTransform = WallpaperTransform(),
)
