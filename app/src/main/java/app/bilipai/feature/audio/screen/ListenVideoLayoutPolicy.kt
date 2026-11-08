package app.bilipai.feature.audio.screen

internal enum class ListenVideoLayout {
    COMPACT_LIST,
    WIDE_GRID
}

internal fun resolveListenVideoLayout(widthDp: Int): ListenVideoLayout {
    return if (widthDp >= 600) {
        ListenVideoLayout.WIDE_GRID
    } else {
        ListenVideoLayout.COMPACT_LIST
    }
}
