package app.bilipai.feature.video.subtitle

import app.bilipai.data.model.response.SubtitleItem

typealias SubtitleCue = app.bilipai.core.subtitle.SubtitleCue
typealias SubtitleWordSpan = app.bilipai.core.subtitle.SubtitleWordSpan
typealias SubtitleLoadResult = app.bilipai.core.subtitle.SubtitleLoadResult
typealias SubtitleTrackMeta = app.bilipai.core.subtitle.SubtitleTrackMeta
typealias SubtitleTrackOption = app.bilipai.core.subtitle.SubtitleTrackOption
typealias SubtitleLanguageSelection = app.bilipai.core.subtitle.SubtitleLanguageSelection
typealias SubtitleDisplayMode = app.bilipai.core.subtitle.SubtitleDisplayMode
typealias SubtitleDisplayOption = app.bilipai.core.subtitle.SubtitleDisplayOption
typealias SubtitlePreferenceSession = app.bilipai.core.subtitle.SubtitlePreferenceSession
typealias SubtitleControlAvailability = app.bilipai.core.subtitle.SubtitleControlAvailability
typealias SubtitleTextSizeSpec = app.bilipai.core.subtitle.SubtitleTextSizeSpec
typealias SubtitleAutoPreference = app.bilipai.core.subtitle.SubtitleAutoPreference

fun normalizeBilibiliSubtitleUrl(raw: String): String = app.bilipai.core.subtitle.normalizeBilibiliSubtitleUrl(raw = raw)

fun isTrustedBilibiliSubtitleUrl(raw: String): Boolean = app.bilipai.core.subtitle.isTrustedBilibiliSubtitleUrl(raw = raw)

fun isLikelyAiSubtitleTrack(track: SubtitleTrackMeta): Boolean = app.bilipai.core.subtitle.isLikelyAiSubtitleTrack(track = track)

fun buildSubtitleTrackKey(
    subtitleId: Long,
    subtitleIdStr: String,
    languageCode: String?,
    subtitleUrl: String
): String = app.bilipai.core.subtitle.buildSubtitleTrackKey(subtitleId = subtitleId, subtitleIdStr = subtitleIdStr, languageCode = languageCode, subtitleUrl = subtitleUrl)

fun mapPlayerInfoSubtitleTracks(subtitles: List<SubtitleItem>): List<SubtitleTrackMeta> = app.bilipai.core.subtitle.mapPlayerInfoSubtitleTracks(subtitles = subtitles)

fun resolveSubtitleTrackDisplayLabel(track: SubtitleTrackMeta): String = app.bilipai.core.subtitle.resolveSubtitleTrackDisplayLabel(track = track)

fun buildSubtitleTrackOptions(
    tracks: List<SubtitleTrackMeta>,
    selectedTrackKey: String?
): List<SubtitleTrackOption> = app.bilipai.core.subtitle.buildSubtitleTrackOptions(tracks = tracks, selectedTrackKey = selectedTrackKey)

fun normalizeSubtitleVerticalOffsetFraction(value: Float): Float = app.bilipai.core.subtitle.normalizeSubtitleVerticalOffsetFraction(value = value)

fun resolveSubtitleTextSizeSpec(
    playerWidthDp: Int,
    largeTextEnabled: Boolean
): SubtitleTextSizeSpec = app.bilipai.core.subtitle.resolveSubtitleTextSizeSpec(playerWidthDp = playerWidthDp, largeTextEnabled = largeTextEnabled)

fun orderSubtitleTracksByPreference(tracks: List<SubtitleTrackMeta>): List<SubtitleTrackMeta> = app.bilipai.core.subtitle.orderSubtitleTracksByPreference(tracks = tracks)

fun parseBiliSubtitleBody(rawJson: String): List<SubtitleCue> = app.bilipai.core.subtitle.parseBiliSubtitleBody(rawJson = rawJson)

fun resolveDefaultSubtitleLanguages(
    tracks: List<SubtitleTrackMeta>,
    preferredPrimaryLanguage: String? = null
): SubtitleLanguageSelection = app.bilipai.core.subtitle.resolveDefaultSubtitleLanguages(tracks = tracks, preferredPrimaryLanguage = preferredPrimaryLanguage)

fun resolveDefaultSubtitleDisplayMode(
    hasPrimaryTrack: Boolean,
    hasSecondaryTrack: Boolean
): SubtitleDisplayMode = app.bilipai.core.subtitle.resolveDefaultSubtitleDisplayMode(hasPrimaryTrack = hasPrimaryTrack, hasSecondaryTrack = hasSecondaryTrack)

fun normalizeSubtitleDisplayMode(
    preferredMode: SubtitleDisplayMode,
    hasPrimaryTrack: Boolean,
    hasSecondaryTrack: Boolean
): SubtitleDisplayMode = app.bilipai.core.subtitle.normalizeSubtitleDisplayMode(preferredMode = preferredMode, hasPrimaryTrack = hasPrimaryTrack, hasSecondaryTrack = hasSecondaryTrack)

fun resolveSubtitleDisplayModeByAutoPreference(
    preference: SubtitleAutoPreference,
    hasPrimaryTrack: Boolean,
    hasSecondaryTrack: Boolean,
    primaryTrackLikelyAi: Boolean,
    secondaryTrackLikelyAi: Boolean,
    isMuted: Boolean,
    primaryTrackLanguage: String?,
    secondaryTrackLanguage: String?
): SubtitleDisplayMode = app.bilipai.core.subtitle.resolveSubtitleDisplayModeByAutoPreference(preference = preference, hasPrimaryTrack = hasPrimaryTrack, hasSecondaryTrack = hasSecondaryTrack, primaryTrackLikelyAi = primaryTrackLikelyAi, secondaryTrackLikelyAi = secondaryTrackLikelyAi, isMuted = isMuted, primaryTrackLanguage = primaryTrackLanguage, secondaryTrackLanguage = secondaryTrackLanguage)

fun resolveSubtitlePreferenceSession(
    bvid: String,
    cid: Long,
    primaryLanguage: String?,
    secondaryLanguage: String?,
    primaryTrackLikelyAi: Boolean,
    secondaryTrackLikelyAi: Boolean,
    hasPrimaryTrack: Boolean,
    hasSecondaryTrack: Boolean,
    preference: SubtitleAutoPreference,
    isMuted: Boolean = false
): SubtitlePreferenceSession = app.bilipai.core.subtitle.resolveSubtitlePreferenceSession(bvid = bvid, cid = cid, primaryLanguage = primaryLanguage, secondaryLanguage = secondaryLanguage, primaryTrackLikelyAi = primaryTrackLikelyAi, secondaryTrackLikelyAi = secondaryTrackLikelyAi, hasPrimaryTrack = hasPrimaryTrack, hasSecondaryTrack = hasSecondaryTrack, preference = preference, isMuted = isMuted)

fun resolveSubtitleDisplayModePreference(
    previousSessionKey: String?,
    nextSessionKey: String,
    previousMode: SubtitleDisplayMode,
    nextInitialMode: SubtitleDisplayMode
): SubtitleDisplayMode = app.bilipai.core.subtitle.resolveSubtitleDisplayModePreference(previousSessionKey = previousSessionKey, nextSessionKey = nextSessionKey, previousMode = previousMode, nextInitialMode = nextInitialMode)

fun resolveSubtitleControlAvailability(
    primaryTrackBound: Boolean,
    secondaryTrackBound: Boolean,
    primaryCueAvailable: Boolean,
    secondaryCueAvailable: Boolean
): SubtitleControlAvailability = app.bilipai.core.subtitle.resolveSubtitleControlAvailability(primaryTrackBound = primaryTrackBound, secondaryTrackBound = secondaryTrackBound, primaryCueAvailable = primaryCueAvailable, secondaryCueAvailable = secondaryCueAvailable)

fun resolveSubtitleDisplayOptions(
    primaryLabel: String,
    secondaryLabel: String,
    hasPrimaryTrack: Boolean,
    hasSecondaryTrack: Boolean
): List<SubtitleDisplayOption> = app.bilipai.core.subtitle.resolveSubtitleDisplayOptions(primaryLabel = primaryLabel, secondaryLabel = secondaryLabel, hasPrimaryTrack = hasPrimaryTrack, hasSecondaryTrack = hasSecondaryTrack)

fun shouldRenderPrimarySubtitle(mode: SubtitleDisplayMode): Boolean = app.bilipai.core.subtitle.shouldRenderPrimarySubtitle(mode = mode)

fun shouldRenderSecondarySubtitle(mode: SubtitleDisplayMode): Boolean = app.bilipai.core.subtitle.shouldRenderSecondarySubtitle(mode = mode)

fun resolveSubtitleTextAt(cues: List<SubtitleCue>, positionMs: Long): String? = app.bilipai.core.subtitle.resolveSubtitleTextAt(cues = cues, positionMs = positionMs)

fun resolveSubtitlePositionPollingIdentity(
    bvid: String?,
    cid: Long,
): String = app.bilipai.core.subtitle.resolveSubtitlePositionPollingIdentity(bvid = bvid, cid = cid)

fun shouldKeepSubtitleOverlayMounted(
    overlayEnabled: Boolean,
    isInPipMode: Boolean,
    isAudioOnly: Boolean,
    suppressOverlay: Boolean,
): Boolean = app.bilipai.core.subtitle.shouldKeepSubtitleOverlayMounted(overlayEnabled = overlayEnabled, isInPipMode = isInPipMode, isAudioOnly = isAudioOnly, suppressOverlay = suppressOverlay)

fun resolveStickySubtitleText(
    currentText: String?,
    previousText: String?,
    blankGapMs: Long,
    maxHoldMs: Long = 280L,
): String? = app.bilipai.core.subtitle.resolveStickySubtitleText(currentText = currentText, previousText = previousText, blankGapMs = blankGapMs, maxHoldMs = maxHoldMs)
