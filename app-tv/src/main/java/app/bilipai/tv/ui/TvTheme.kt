@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package app.bilipai.tv.ui

import android.app.ActivityManager
import androidx.compose.ui.platform.LocalContext
import app.bilipai.core.ui.motion.rememberSystemReduceMotion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import app.bilipai.core.theme.BiliPink
import app.bilipai.core.theme.BiliPinkDark
import app.bilipai.core.theme.DarkBackground
import app.bilipai.core.theme.DarkSurface
import app.bilipai.core.theme.DarkSurfaceVariant
import app.bilipai.core.theme.TextPrimaryDark
import app.bilipai.core.theme.TextSecondaryDark
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun TvTheme(
    reduceMotion: Boolean = false,
    simpleEffects: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemReduceMotion = rememberSystemReduceMotion()
    val lowRam = (LocalContext.current.getSystemService(android.content.Context.ACTIVITY_SERVICE) as? ActivityManager)?.isLowRamDevice == true
    // 动态取色：开启且提取成功时强调色跟随壁纸/封面，否则品牌粉；文字与表面体系保持不变。
    val dynamicAccent by TvAmbientColorStore.accent.collectAsState()
    MaterialTheme(shapes = TvUiTokens.shapes, typography = TvUiTokens.typography, colorScheme = darkColorScheme(
        primary = dynamicAccent ?: BiliPink, onPrimary = DarkBackground,
        background = DarkBackground, onBackground = TextPrimaryDark,
        surface = DarkBackground, onSurface = TextPrimaryDark,
        surfaceVariant = DarkSurface, onSurfaceVariant = TextPrimaryDark,
        secondary = TextSecondaryDark, secondaryContainer = DarkSurfaceVariant,
        onSecondaryContainer = TextPrimaryDark,
        border = dynamicAccent?.let(::resolveTvDynamicBorder) ?: BiliPinkDark,
    )) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface,
            LocalTvReduceMotion provides (reduceMotion || systemReduceMotion),
            LocalTvSimpleEffects provides (simpleEffects || lowRam),
            content = content,
        )
    }
}
