package app.bilipai.feature.home.components.cards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import app.bilipai.core.ui.MediaContrastPalette
import app.bilipai.core.ui.components.AppText
import app.bilipai.core.ui.feedContentTypography

/** Cover duration: white text + soft shadow, matching home feed overlay (no black capsule). */
@Composable
internal fun VideoCardCoverDurationText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val overlayStyle = remember {
        TextStyle(shadow = resolveVideoCardCoverOverlayTextShadow())
    }
    AppText(
        text = text,
        color = MediaContrastPalette.Foreground,
        style = feedContentTypography().coverBadge
            .copy(fontWeight = FontWeight.Medium)
            .merge(overlayStyle),
        maxLines = 1,
        tapToCopyEnabled = false,
        modifier = modifier,
    )
}
