package app.bilipai.feature.video.policy

import app.bilipai.data.model.response.FavFolder

internal fun resolveFavoriteFolderMediaId(folder: FavFolder): Long {
    return when {
        // x/v3/fav/resource/deal 需要 media_id（收藏夹 ID），优先使用 id。
        folder.id > 0L -> folder.id
        folder.fid > 0L -> folder.fid
        else -> 0L
    }
}
