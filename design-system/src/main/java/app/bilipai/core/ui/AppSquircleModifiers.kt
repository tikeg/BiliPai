package app.bilipai.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.theme.shouldUseMiuixSmoothRounding
import top.yukonga.miuix.kmp.squircle.squircleBackground

@Composable
fun Modifier.adaptiveSquircleBackground(
    color: Color,
    cornerRadius: Dp
): Modifier {
    return when (LocalAppUiStyle.current) {
        AppUiStyle.MIUIX -> {
            squircleBackground(color = color, cornerRadius = cornerRadius)
        }
        AppUiStyle.MATERIAL3 -> {
            clip(RoundedCornerShape(cornerRadius))
                .background(color)
        }
    }
}

internal fun shouldApplyMiuixSquircleBackground(
    uiStyle: AppUiStyle
): Boolean = shouldUseMiuixSmoothRounding(uiStyle)
