package app.bilipai.feature.bangumi

import app.bilipai.data.model.response.Durl

fun collectPlayableDurlUrls(durlList: List<Durl>?): List<String> {
    if (durlList.isNullOrEmpty()) return emptyList()
    return durlList.mapNotNull { durl ->
        val primary = durl.url.takeIf { it.isNotBlank() }
        val backup = durl.backupUrl?.firstOrNull { it.isNotBlank() }
        primary ?: backup
    }
}
