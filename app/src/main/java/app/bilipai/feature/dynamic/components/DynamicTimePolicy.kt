package app.bilipai.feature.dynamic.components

import app.bilipai.core.util.FormatUtils

internal fun resolveDynamicAuthorTimeText(
    pubTime: String,
    pubTs: Long,
    nowMs: Long = System.currentTimeMillis()
): String {
    return if (pubTs > 0L) {
        FormatUtils.formatPublishTime(timestampSeconds = pubTs, nowMs = nowMs)
    } else {
        pubTime
    }
}
