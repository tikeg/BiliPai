package app.bilipai.feature.dynamic.components

import app.bilipai.data.model.response.ArchiveMajor

internal fun resolveDynamicArchiveBadgeLabel(archive: ArchiveMajor): String? {
    archive.badge?.text?.trim()
        ?.takeIf { it.isNotBlank() && it != "投稿视频" }
        ?.let { return it }
    return if (archive.isChargingArc ||
        archive.elecArcType == 1 ||
        archive.isUgcpay ||
        archive.ugcPay > 0 ||
        archive.ugcPayPreview > 0
    ) {
        "充电专属"
    } else {
        null
    }
}
