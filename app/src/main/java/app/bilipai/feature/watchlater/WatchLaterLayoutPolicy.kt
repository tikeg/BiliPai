package app.bilipai.feature.watchlater

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.bilipai.feature.personal.resolvePersonalListColumnCount

internal fun resolveWatchLaterListMaxWidth(): Dp = 480.dp

internal fun resolveWatchLaterColumnCount(availableWidthDp: Float): Int =
    resolvePersonalListColumnCount(availableWidthDp)
