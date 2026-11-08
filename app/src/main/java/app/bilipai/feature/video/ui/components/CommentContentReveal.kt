package app.bilipai.feature.video.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.bilipai.core.ui.LocalAppThemeConfig
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.motion.AppMotionTokens
import app.bilipai.core.ui.rememberAppBottomSheetMotion

/** Inline expansion, with one owner for both height and content lifetime. */
@Composable
internal fun CommentContentReveal(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val uiStyle = LocalAppUiStyle.current
    val sheetMotion = rememberAppBottomSheetMotion()
    val reducedMotion = sheetMotion.reducedMotion ||
        !LocalAppThemeConfig.current.uiEntranceAnimationEnabled
    // Visibility owns both layout and content lifetime. Repeated taps retarget the
    // current transition; an outer animateContentSize must not chase the same height.
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (reducedMotion) {
            fadeIn(AppMotionTokens.resolveBottomSheetFadeEnterSpec(uiStyle))
        } else if (uiStyle == AppUiStyle.MIUIX) {
            // Shared with the cascading conversation transition in both material modes.
            // Keep the MD3 branch's existing motion and all native layout parameters.
            expandVertically(
                animationSpec = commentCascadeSpring(true),
                expandFrom = Alignment.Top,
            ) + fadeIn(commentCascadeSpring(true))
        } else {
            fadeIn(AppMotionTokens.resolveBottomSheetFadeEnterSpec(uiStyle)) +
                expandVertically(
                    animationSpec = AppMotionTokens.resolveSpatialSpec(),
                    expandFrom = Alignment.Bottom,
                ) + slideInVertically(
                    animationSpec = AppMotionTokens.resolveSpatialSpec(),
                    initialOffsetY = { it / 6 },
                )
        },
        exit = if (reducedMotion) {
            fadeOut(AppMotionTokens.resolveBottomSheetFadeExitSpec(uiStyle))
        } else if (uiStyle == AppUiStyle.MIUIX) {
            shrinkVertically(
                animationSpec = commentCascadeSpring(false),
                shrinkTowards = Alignment.Top,
            ) + fadeOut(commentCascadeSpring(false))
        } else {
            fadeOut(AppMotionTokens.resolveBottomSheetFadeExitSpec(uiStyle)) +
                shrinkVertically(
                    animationSpec = AppMotionTokens.resolveExpressiveSpec(uiStyle),
                    shrinkTowards = Alignment.Bottom,
                ) + slideOutVertically(
                    animationSpec = AppMotionTokens.resolveExpressiveSpec(uiStyle),
                    targetOffsetY = { it / 6 },
                )
        },
    ) {
        content()
    }
}
