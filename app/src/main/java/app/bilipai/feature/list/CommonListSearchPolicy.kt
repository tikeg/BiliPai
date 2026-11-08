package app.bilipai.feature.list

import app.bilipai.core.util.PinyinUtils
import app.bilipai.data.model.response.VideoItem

internal fun filterCommonListVideosByQuery(
    items: List<VideoItem>,
    query: String
): List<VideoItem> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isBlank()) return items

    return items.filter { item ->
        PinyinUtils.matches(item.title, normalizedQuery) ||
            PinyinUtils.matches(item.owner.name, normalizedQuery)
    }
}

internal fun shouldLoadMoreCommonListSearchResults(
    searchQuery: String,
    filteredItemCount: Int,
    hasMore: Boolean,
    isLoadingMore: Boolean
): Boolean {
    return searchQuery.isNotBlank() &&
        filteredItemCount == 0 &&
        hasMore &&
        !isLoadingMore
}
