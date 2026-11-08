package app.bilipai.core.refresh

import app.bilipai.core.store.AccountSessionIdentity
import app.bilipai.data.model.response.FavFolder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal data class FavoriteSubscriptionChange(
    val identity: AccountSessionIdentity,
    val folder: FavFolder,
    val subscribed: Boolean,
)

/** 同账号的订阅目录与独立搜索页同步已确认的操作结果，并保留取消后的撤销条目。 */
internal object FavoriteSubscriptionBus {
    private val _changes = MutableSharedFlow<FavoriteSubscriptionChange>(extraBufferCapacity = 1)
    val changes = _changes.asSharedFlow()

    suspend fun notifyChanged(identity: AccountSessionIdentity, folder: FavFolder, subscribed: Boolean) {
        _changes.emit(FavoriteSubscriptionChange(identity, folder, subscribed))
    }
}
