package app.bilipai.data.repository

import app.bilipai.core.network.NetworkModule
import app.bilipai.core.network.buildSpaceLikedArchiveParams
import app.bilipai.core.store.TokenManager
import app.bilipai.data.model.response.SpaceAggregateArchive

object SpaceProfileRepository {
    suspend fun followedUsers(mid: Long, page: Int): Result<app.bilipai.data.model.response.FollowingsData> = dataRequest {
        val response = NetworkModule.api.getSpaceFollowedUsers(mid, page, TokenManager.csrfCache.orEmpty())
        if (response.code != 0) throw ContentRequestException(response.code, response.message)
        response.data ?: error("关注列表为空")
    }
    suspend fun specialFollow(mid: Long, add: Boolean): Result<Unit> = dataRequest {
        val csrf = TokenManager.csrfCache.orEmpty()
        val response = if (add) NetworkModule.api.addSpaceSpecialFollow(mid, csrf) else NetworkModule.api.removeSpaceSpecialFollow(mid, csrf)
        if (response.code != 0) throw ContentRequestException(response.code, response.message)
    }
    suspend fun followTime(mid: Long): Result<Long> = dataRequest {
        val response = NetworkModule.api.getRelation(mid)
        if (response.code != 0) throw ContentRequestException(response.code, response.message)
        response.data?.mtime?.takeIf { it > 0 } ?: error("暂无可查询的关注时间")
    }

    suspend fun reserve(sid: Long, cancel: Boolean): Result<Unit> = dataRequest {
        val csrf = TokenManager.csrfCache.orEmpty()
        check(csrf.isNotBlank()) { "请先登录后预约" }
        val response = if (cancel) NetworkModule.api.cancelSpaceReserve(sid, csrf) else NetworkModule.api.reserveSpace(sid, csrf)
        if (response.code != 0) throw ContentRequestException(response.code, response.message)
    }

    suspend fun claimVipExperience(): Result<Unit> = dataRequest {
        val mid = TokenManager.midCache ?: error("请先登录")
        val response = NetworkModule.api.claimVipExperience(mid, TokenManager.csrfCache.orEmpty())
        if (response.code != 0) throw ContentRequestException(response.code, response.message)
    }

    suspend fun medalWall(mid: Long): Result<app.bilipai.data.model.response.SpaceMedalWall> = dataRequest {
        val response = NetworkModule.api.getSpaceMedalWall(mid)
        if (response.code != 0) throw ContentRequestException(response.code, response.message)
        response.data ?: error("勋章墙信息为空")
    }
    suspend fun opus(mid: Long, offset: String, type: String): Result<app.bilipai.core.network.grpc.SpaceOpusPage> = dataRequest {
        app.bilipai.core.network.grpc.SpaceOpusApi.load(mid, offset, type)
    }
    suspend fun comics(mid: Long): Result<SpaceAggregateArchive> = dataRequest {
        val params = buildSpaceLikedArchiveParams(mid = mid, accessToken = TokenManager.accessTokenCache,
            accessTokenPlatform = TokenManager.accessTokenPlatformCache)
        val response = NetworkModule.spaceApi.getSpaceComics(params)
        if (response.code != 0) throw ContentRequestException(response.code, response.message)
        response.data ?: error("漫画列表为空")
    }
}
