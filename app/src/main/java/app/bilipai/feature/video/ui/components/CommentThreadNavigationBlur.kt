package app.bilipai.feature.video.ui.components

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import app.bilipai.core.ui.transition.resolvePredictiveBackBlurFrame
import app.bilipai.core.ui.transition.BlurRenderEffectCache

/** Blur the covered comment content, never the foreground thread or its controls. */
internal fun Modifier.commentThreadNavigationBlur(
    progress: () -> Float,
): Modifier {
    val effectCache = BlurRenderEffectCache()
    return graphicsLayer {
        renderEffect = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val frame = resolvePredictiveBackBlurFrame(progress = progress())
            if (frame.blurRadiusPx > 0.5f) {
                renderEffect = effectCache.resolve(frame.blurRadiusPx)
            }
        }
    }
}
