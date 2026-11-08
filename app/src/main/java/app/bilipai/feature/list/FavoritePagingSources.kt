package app.bilipai.feature.list

import androidx.paging.PagingSource
import app.bilipai.core.paging.ForwardPage
import app.bilipai.core.paging.ForwardPagingSource
import app.bilipai.data.model.response.FavFolder
import app.bilipai.data.repository.FavoriteRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal data class SubscribedFolderPageChunk(val number: Int, val folders: List<FavFolder>, val totalCount: Int)

internal fun subscribedFolderPagingSource(
    pageSize: Int,
    loadPage: suspend (Int) -> FavoriteRepository.CollectedFavFoldersPage,
): PagingSource<Int, SubscribedFolderPageChunk> {
    val seen = mutableSetOf<String>()
    return ForwardPagingSource(1) { number ->
        val page = loadPage(number)
        currentCoroutineContext().ensureActive()
        val fresh = page.folders.filter { seen.add(resolveSubscribedFavoriteFolderKey(it)) }
        val more = fresh.isNotEmpty() && if (page.totalCount > 0) seen.size < page.totalCount else page.folders.size >= pageSize
        ForwardPage(listOf(SubscribedFolderPageChunk(number, fresh, page.totalCount)), (number + 1).takeIf { more })
    }
}
