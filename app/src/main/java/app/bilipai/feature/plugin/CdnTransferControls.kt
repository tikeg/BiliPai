package app.bilipai.feature.plugin

import app.bilipai.plugin.sdk.Plugin

interface CdnTransferControls : Plugin {
    fun canUseParallelDownload(): Boolean
    suspend fun setParallelDownloadEnabled(enabled: Boolean)
}
