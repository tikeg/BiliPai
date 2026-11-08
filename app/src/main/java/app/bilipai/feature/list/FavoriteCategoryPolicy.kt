package app.bilipai.feature.list

import app.bilipai.data.model.response.FavoriteSection
import app.bilipai.data.model.response.FavoriteSearchScope
import app.bilipai.feature.personal.resolvePersonalListColumnCount

internal fun resolveFavoriteCategoryColumnCount(
    section: FavoriteSection,
    availableWidthDp: Float,
): Int = when (section) {
    FavoriteSection.BANGUMI, FavoriteSection.CINEMA ->
        (availableWidthDp / 180f).toInt().coerceAtLeast(2)
    else -> resolvePersonalListColumnCount(availableWidthDp)
}

internal fun supportsFavoriteCategoryBatchSelection(section: FavoriteSection): Boolean =
    section == FavoriteSection.BANGUMI ||
        section == FavoriteSection.CINEMA ||
        section == FavoriteSection.NOTE

internal fun opensFavoriteCategoryInNativeScreen(section: FavoriteSection): Boolean = when (section) {
    FavoriteSection.VIDEO,
    FavoriteSection.BANGUMI,
    FavoriteSection.CINEMA,
    FavoriteSection.ARTICLE,
    FavoriteSection.TOPIC,
    FavoriteSection.COURSE -> true
    FavoriteSection.NOTE -> false
}

internal fun resolveFavoriteSearchApiType(scope: FavoriteSearchScope): Int = when (scope) {
    FavoriteSearchScope.CURRENT_FOLDER -> 0
    FavoriteSearchScope.ALL_VIDEO_FOLDERS -> 1
}
