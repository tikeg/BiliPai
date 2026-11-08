package app.bilipai.feature.article

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.bilipai.core.ui.resolveBottomSafeAreaPadding

internal fun resolveArticleDetailBottomPadding(
    navigationBarsBottom: Dp,
    extraBottomPadding: Dp = 24.dp
): Dp {
    return resolveBottomSafeAreaPadding(
        navigationBarsBottom = navigationBarsBottom,
        extraBottomPadding = extraBottomPadding
    )
}

internal fun resolveArticleImageAspectRatio(
    width: Int,
    height: Int
): Float? {
    if (width <= 0 || height <= 0) return null
    return width.toFloat() / height.toFloat()
}
