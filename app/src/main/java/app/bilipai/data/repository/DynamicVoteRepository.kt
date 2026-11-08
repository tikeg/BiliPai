package app.bilipai.data.repository

import app.bilipai.core.network.NetworkModule
import app.bilipai.core.store.TokenManager
import app.bilipai.data.model.response.DynamicDoVoteRequest
import app.bilipai.data.model.response.DynamicVoteInfo
import app.bilipai.data.model.response.toResolvedVoteInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DynamicVoteRepository {
    suspend fun getVoteInfo(voteId: Long): Result<DynamicVoteInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val response = NetworkModule.dynamicApi.getVoteInfo(voteId)
            if (response.code != 0) {
                error(response.message.ifBlank { "投票信息加载失败" })
            }
            response.data?.toResolvedVoteInfo()
                ?: error("投票信息为空")
        }.onFailure { if (it is CancellationException) throw it }
    }

    suspend fun submitVote(
        voteId: Long,
        optionIndexes: List<Int>,
        dynamicId: String = "",
        anonymous: Boolean = false
    ): Result<DynamicVoteInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val csrf = TokenManager.csrfCache.orEmpty()
            if (csrf.isBlank()) error("请先登录")
            val response = NetworkModule.dynamicApi.doVote(
                csrf = csrf,
                body = DynamicDoVoteRequest(
                    vote_id = voteId,
                    votes = optionIndexes,
                    voter_uid = TokenManager.midCache ?: 0L,
                    status = if (anonymous) 1 else 0,
                    dynamic_id = dynamicId.trim().toLongOrNull() ?: 0L,
                    csrf = csrf,
                    csrf_token = csrf
                )
            )
            if (response.code != 0) {
                error(response.message.ifBlank { "投票失败" })
            }
            response.data?.toResolvedVoteInfo()
                ?: error("投票结果为空")
        }.onFailure { if (it is CancellationException) throw it }
    }
}