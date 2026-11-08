package app.bilipai.feature.space

import app.bilipai.data.model.response.SpaceVideoItem

internal fun resolveSpaceVideoChargeBadgeLabel(video: SpaceVideoItem): String? {
    return if (video.isChargingArc ||
        video.elecArcType == 1 ||
        video.isUgcpay ||
        video.ugcPay > 0 ||
        video.ugcPayPreview > 0
    ) {
        "充电专属"
    } else {
        null
    }
}
