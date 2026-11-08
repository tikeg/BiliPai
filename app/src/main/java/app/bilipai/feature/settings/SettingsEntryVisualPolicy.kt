package app.bilipai.feature.settings

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.bilipai.core.ui.AppContentColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import app.bilipai.R
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.theme.iOSBlue
import app.bilipai.core.theme.iOSGreen
import app.bilipai.core.theme.iOSOrange
import app.bilipai.core.theme.iOSPink
import app.bilipai.core.theme.iOSPurple
import app.bilipai.core.theme.iOSRed
import app.bilipai.core.theme.iOSTeal
import app.bilipai.core.ui.AppIcons
import app.bilipai.core.ui.AppSemanticAccentRole
import app.bilipai.core.ui.AppSemanticIconFamily
import app.bilipai.core.ui.AppSemanticVisualPolicy
import app.bilipai.core.ui.rememberAppSemanticVisualPolicy

internal data class SettingsEntryVisual(
    val icon: ImageVector? = null,
    @DrawableRes val iconResId: Int? = null,
    val iconTint: Color,
    val iconSizeDp: Int = 20,
)

/**
 * 设置入口图标色：Miuix 沿用按入口分配的七彩色，Material 3 统一使用中性内容色。
 */
@Composable
internal fun rememberSettingsEntryTint(
    role: AppSemanticAccentRole,
    iosTint: Color,
): Color {
    val uiStyle = LocalAppUiStyle.current
    val neutralColor = AppContentColors.secondaryText()
    return remember(role, iosTint, uiStyle, neutralColor) {
        if (uiStyle == AppUiStyle.MIUIX) iosTint else neutralColor
    }
}

private fun resolveIosSettingsEntryTint(
    target: SettingsSearchTarget
): Color = when (target) {
    SettingsSearchTarget.INTERFACE_THEME -> iOSPink
    SettingsSearchTarget.HOME_FEED -> iOSOrange
    SettingsSearchTarget.NAVIGATION -> iOSBlue
    SettingsSearchTarget.PLAYBACK_QUALITY -> iOSGreen
    SettingsSearchTarget.FULLSCREEN_GESTURE -> iOSPurple
    SettingsSearchTarget.INTERACTION_COMMENT -> iOSTeal
    SettingsSearchTarget.DATA_BACKUP -> iOSBlue
    SettingsSearchTarget.PRIVACY_PERMISSION -> iOSPurple
    SettingsSearchTarget.DIAGNOSTICS -> iOSTeal
    SettingsSearchTarget.ABOUT_SUPPORT -> iOSOrange
    SettingsSearchTarget.APPEARANCE -> iOSPink
    SettingsSearchTarget.ANIMATION -> iOSPink
    SettingsSearchTarget.PLAYBACK -> iOSGreen
    SettingsSearchTarget.BOTTOM_BAR -> iOSBlue
    SettingsSearchTarget.PERMISSION -> iOSTeal
    SettingsSearchTarget.SPACE_PRIVACY -> iOSGreen
    SettingsSearchTarget.LOGIN_ACTIVITY -> iOSGreen
    SettingsSearchTarget.MESSAGE_NOTIFICATION -> iOSBlue
    SettingsSearchTarget.BLOCKED_LIST -> iOSBlue
    SettingsSearchTarget.SETTINGS_SHARE -> iOSGreen
    SettingsSearchTarget.WEBDAV_BACKUP -> iOSBlue
    SettingsSearchTarget.DOWNLOAD_PATH -> iOSBlue
    SettingsSearchTarget.IMAGE_SAVE_PATH -> iOSTeal
    SettingsSearchTarget.CLEAR_CACHE -> iOSBlue
    SettingsSearchTarget.PLUGINS -> iOSPurple
    SettingsSearchTarget.NEARBY_HANDOFF -> iOSTeal
    SettingsSearchTarget.EXPORT_LOGS -> iOSTeal
    SettingsSearchTarget.OPEN_SOURCE_LICENSES -> iOSOrange
    SettingsSearchTarget.OPEN_SOURCE_HOME -> iOSPurple
    SettingsSearchTarget.CHECK_UPDATE -> iOSBlue
    SettingsSearchTarget.VIEW_RELEASE_NOTES -> iOSTeal
    SettingsSearchTarget.REPLAY_ONBOARDING -> iOSPink
    SettingsSearchTarget.TIPS -> iOSOrange
    SettingsSearchTarget.OPEN_LINKS -> iOSTeal
    SettingsSearchTarget.DONATE -> iOSRed
    SettingsSearchTarget.TELEGRAM -> iOSBlue
    SettingsSearchTarget.TWITTER -> iOSBlue
    SettingsSearchTarget.DISCLAIMER -> iOSBlue
}

@Composable
internal fun rememberSettingsEntryVisual(
    target: SettingsSearchTarget,
): SettingsEntryVisual {
    val policy = rememberAppSemanticVisualPolicy()
    val neutralColor = AppContentColors.secondaryText()
    val colorful = LocalAppUiStyle.current == AppUiStyle.MIUIX
    val resolved = remember(target, policy, neutralColor, colorful) {
        resolveSettingsEntryVisual(target, policy, neutralColor, colorful)
    }
    // Material 3's native ListItem renders ImageVector leading content more
    // consistently than a VectorDrawable painter (whose XML tint can be
    // overridden by the platform theme). Keep the resource for legacy skins,
    // but also expose the vector form so MD3 rows never lose their glyph.
    return if (resolved.icon == null && resolved.iconResId != null) {
        resolved.copy(icon = rememberMaterialSymbol(resolved.iconResId))
    } else {
        resolved
    }
}

/**
 * @param neutralColor 非七彩主题（Material 3）使用的中性图标色。
 * @param colorful Miuix 传 true 取按入口分配的彩色，其余主题传 false 使用中性色。
 */
internal fun resolveSettingsEntryVisual(
    target: SettingsSearchTarget,
    policy: AppSemanticVisualPolicy = AppSemanticVisualPolicy(
        iconFamily = AppSemanticIconFamily.MATERIAL,
        accentPalette = null,
        prefersNativeChrome = false,
        supportsIndependentLiquidGlass = true,
    ),
    neutralColor: Color = Color.Unspecified,
    colorful: Boolean = false,
): SettingsEntryVisual {
    val iconTint = if (colorful) resolveIosSettingsEntryTint(target) else neutralColor
    return when (target) {
        SettingsSearchTarget.TELEGRAM -> SettingsEntryVisual(
            iconResId = R.drawable.ic_telegram_mono,
            iconTint = iconTint
        )
        SettingsSearchTarget.TWITTER -> SettingsEntryVisual(
            icon = AppIcons.Twitter,
            iconTint = iconTint
        )
        else -> {
            val role = resolveSettingsSearchTargetIconRole(target)
            SettingsEntryVisual(
                iconResId = resolveSettingsMaterialSymbolResource(role),
                iconTint = iconTint,
                iconSizeDp = resolveSettingsSemanticIconSizeDp(role, policy.effectiveIconFamily),
            )
        }
    }
}
