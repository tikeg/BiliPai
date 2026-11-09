package app.bilipai.core.navigation

internal fun resolveBottomPagerNavigationDurationMillis(pageDistance: Int): Int {
    val distance = pageDistance.coerceAtLeast(2)
    return distance * 100 + 100
}
