package dev.example.todaywatchremix

import app.bilipai.plugin.sdk.PluginCapability
import app.bilipai.plugin.sdk.PluginCapabilityManifest
import app.bilipai.plugin.sdk.PluginVideoCandidate
import app.bilipai.plugin.sdk.RecommendationMode
import app.bilipai.plugin.sdk.RecommendationPluginApi
import app.bilipai.plugin.sdk.RecommendationRequest
import app.bilipai.plugin.sdk.RecommendationResult
import app.bilipai.plugin.sdk.RecommendedVideo

class TodayWatchRemixPlugin : RecommendationPluginApi<PluginVideoCandidate> {
    override val capabilityManifest = PluginCapabilityManifest(
        pluginId = "dev.example.today_watch_remix",
        displayName = "Today Watch Remix",
        version = "1.0.0",
        apiVersion = 1,
        entryClassName = "dev.example.todaywatchremix.TodayWatchRemixPlugin",
        capabilities = setOf(
            PluginCapability.RECOMMENDATION_CANDIDATES,
            PluginCapability.LOCAL_HISTORY_READ
        )
    )

    override fun buildRecommendations(request: RecommendationRequest<PluginVideoCandidate>): RecommendationResult<PluginVideoCandidate> {
        val ranked = request.candidateVideos
            .filterNot { candidate -> candidate.bvid in request.feedbackSignals.consumedBvids }
            .sortedWith(
                compareByDescending<PluginVideoCandidate> {
                    it.likeCount
                }.thenByDescending { it.playCount }
            )
            .take(request.queueLimit)
            .mapIndexed { index, candidate ->
                RecommendedVideo(
                    video = candidate,
                    score = 100.0 - index,
                    confidence = 0.7f,
                    explanation = "Remix rank: likes first, then plays"
                )
            }

        return RecommendationResult(
            sourcePluginId = capabilityManifest.pluginId,
            mode = request.mode.takeIf { it == RecommendationMode.LEARN } ?: RecommendationMode.RELAX,
            items = ranked,
            historySampleCount = request.historyVideos.size,
            sceneSignals = request.sceneSignals,
            generatedAt = request.sceneSignals.nowEpochSec * 1000L
        )
    }
}
