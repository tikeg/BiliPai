package app.bilipai.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.AppContentColors
import app.bilipai.core.ui.components.AppPreferenceIconPalette
import app.bilipai.core.ui.components.preferenceIconColorForKey
import app.bilipai.core.ui.components.AppPreferenceLeadingIcon
import app.bilipai.core.ui.components.LocalAppPreferenceIconColors

private fun settingsCategoryIconColor(category: SettingsRootCategory): Color =
    AppPreferenceIconPalette[when (canonicalSettingsRootCategory(category)) {
        SettingsRootCategory.APPEARANCE_THEME -> 9
        SettingsRootCategory.PLAYBACK_QUALITY -> 2
        SettingsRootCategory.HOME_RECOMMENDATION -> 1
        SettingsRootCategory.NAVIGATION_INTERACTION -> 0
        SettingsRootCategory.PRIVACY_PERMISSION -> 3
        SettingsRootCategory.STORAGE_BACKUP -> 6
        SettingsRootCategory.PLUGINS_EXTENSIONS -> 4
        else -> 7
    }]

/** Shared names from the directory keep page, search and tablet colors in sync. */
private val settingsFunctionIconColors: Map<String, Color> by lazy {
    buildMap {
        settingsItemDirectory.forEach { item ->
            // Functional names keep semantic colors stable across page and search.
            put(item.title, preferenceIconColorForKey(item.title))
        }
        SettingsSearchTarget.entries.forEach { target ->
            put(settingsDestinationCopy(target).title, preferenceIconColorForKey(settingsDestinationCopy(target).title))
        }
        resolveSettingsRootCategoryOrder().forEach { category -> put(category.title, settingsCategoryIconColor(category)) }
        // 首页九个入口各有独立色组；同一入口在搜索与侧栏保持一致。
        put(settingsDestinationCopy(SettingsSearchTarget.DONATE).title, AppPreferenceIconPalette[5])
    }
}

@Composable
internal fun settingsIconColors(): Map<String, Color>? =
    if (LocalAppUiStyle.current == AppUiStyle.MIUIX ||
        app.bilipai.core.ui.LocalAppIconStyle.current in setOf(
            app.bilipai.core.ui.AppIconStyle.SOFT_COLORFUL,
            app.bilipai.core.ui.AppIconStyle.VIVID_COLORFUL,
        )
    ) settingsFunctionIconColors else null

/** Also works for dialogs composed beside the page scaffold and the tablet sidebar. */
@Composable
internal fun SettingsFunctionIcon(
    imageVector: ImageVector?,
    title: String,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    contentDescription: String? = null,
    tint: Color = AppContentColors.primaryText(),
    nativeSize: Dp = 24.dp,
) {
    // Miuix opts in by default; explicit colorful choices also opt MD3 in. Both
    // glass states share icon geometry. AUTO retains native MD3 defaults.
    CompositionLocalProvider(LocalAppPreferenceIconColors provides settingsIconColors()) {
        AppPreferenceLeadingIcon(
            title = title,
            imageVector = imageVector,
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
            nativeSize = nativeSize,
            modifier = modifier,
        )
    }
}
