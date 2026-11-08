package app.bilipai.feature.live

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.AppIconStyle
import app.bilipai.core.ui.AppThemeConfig
import app.bilipai.core.ui.LocalAppIconStyle
import app.bilipai.core.ui.LocalAppThemeConfig
import app.bilipai.core.ui.ProvideAppContentColors

/** 直播间保留深色播放配色，菜单材质跟随用户液态玻璃开关。 */
internal fun liveRoomThemeConfig(appConfig: AppThemeConfig): AppThemeConfig = appConfig.copy(
    liquidControlsUseThemeColor = false,
    nativeMiuixPopupsEnabled = false,
    headerBlurEnabled = false,
    bottomBarBlurEnabled = false,
    progressiveTopBlurEnabled = false,
    progressiveTopFadeEnabled = false,
)

@Composable
internal fun LiveRoomTheme(content: @Composable () -> Unit) {
    val accent = MaterialTheme.colorScheme
    val roomColors = darkColorScheme(
        primary = accent.primary,
        onPrimary = accent.onPrimary,
        secondary = accent.secondary,
        tertiary = accent.tertiary,
    )
    CompositionLocalProvider(
        LocalAppThemeConfig provides liveRoomThemeConfig(LocalAppThemeConfig.current),
        LocalAppUiStyle provides AppUiStyle.MATERIAL3,
        LocalAppIconStyle provides AppIconStyle.MD3_STANDARD,
    ) {
        MaterialTheme(colorScheme = roomColors) { ProvideAppContentColors(content = content) }
    }
}
