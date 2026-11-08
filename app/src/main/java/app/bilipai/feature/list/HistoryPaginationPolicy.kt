package app.bilipai.feature.list

import app.bilipai.data.model.response.HistoryItem

internal fun filterAppendableHistoryItems(
    currentRenderKeys: Set<String>,
    incomingItems: List<HistoryItem>
): List<HistoryItem> {
    val seenKeys = currentRenderKeys
        .map(String::trim)
        .filter(String::isNotEmpty)
        .toMutableSet()

    return incomingItems.filter { item ->
        seenKeys.add(resolveHistoryRenderKey(item))
    }
}
