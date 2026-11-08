package app.bilipai.feature.video.ui.components

internal fun resolvePlayerSleepTimerPresets(minutes: Int?): List<Int> =
    (listOf(0, 15, 30, 45, 60) + listOfNotNull(minutes)).distinct().sorted()
