package app.bilipai.feature.following

import app.bilipai.core.ui.OfficialVerifyBadgeSpec
import app.bilipai.core.ui.OfficialVerifyBadgeTone
import app.bilipai.core.ui.resolveOfficialVerifyBadge
import app.bilipai.data.model.response.OfficialVerify
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val FollowingSinceFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

typealias FollowingOfficialVerifyBadgeTone = OfficialVerifyBadgeTone
typealias FollowingOfficialVerifyBadge = OfficialVerifyBadgeSpec

internal fun resolveFollowingOfficialVerifyBadge(
    officialVerify: OfficialVerify
): FollowingOfficialVerifyBadge? {
    return resolveOfficialVerifyBadge(
        type = officialVerify.type,
        desc = officialVerify.desc
    )
}

internal fun formatFollowingSinceLabel(
    mtimeSeconds: Long,
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    if (mtimeSeconds <= 0L) return ""
    val date = Instant.ofEpochSecond(mtimeSeconds).atZone(zoneId).toLocalDate()
    return "关注于 ${FollowingSinceFormatter.format(date)}"
}
