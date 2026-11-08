package app.bilipai.feature.profile

import app.bilipai.data.model.response.SplashItem

internal fun resolveOfficialWallpaperThumbnailUrl(item: SplashItem): String {
    return normalizeSplashWallpaperUrl(item.thumb.ifEmpty { item.image })
}

internal fun resolveOfficialWallpaperDetailUrl(item: SplashItem): String {
    return normalizeSplashWallpaperUrl(item.image.ifEmpty { item.thumb })
}
