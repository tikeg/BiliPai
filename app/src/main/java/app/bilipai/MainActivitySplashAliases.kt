package app.bilipai

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

internal const val EXTRA_LAUNCHER_COMPONENT = "app.bilipai.extra.LAUNCHER_COMPONENT"

internal fun resolveSplashThemeResIdForActivityClassName(className: String?): Int {
    return when (className) {
        "app.bilipai.MainActivitySplashBlueSnowMaid" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaid
        "app.bilipai.MainActivitySplashBlueSnowMaidAnnouncement" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaidAnnouncement
        "app.bilipai.MainActivitySplashBlueSnowMaidAnnouncementLight" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaidAnnouncementLight
        "app.bilipai.MainActivitySplashBlueSnowMaidAnnouncementDark" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaidAnnouncementDark
        "app.bilipai.MainActivitySplashBlueSnowMaidFront" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaidFront
        "app.bilipai.MainActivitySplashBlueSnowMaidLight" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaidLight
        "app.bilipai.MainActivitySplashBlueSnowMaidDark" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaidDark
        "app.bilipai.MainActivitySplashBlueSnowMaidFrontLight" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaidFrontLight
        "app.bilipai.MainActivitySplashBlueSnowMaidFrontDark" -> R.style.Theme_PureBiliBili_Splash_BlueSnowMaidFrontDark
        "app.bilipai.MainActivitySplashIcon3D" -> R.style.Theme_PureBiliBili_Splash_Icon3D
        "app.bilipai.MainActivitySplashIcon3DLight" -> R.style.Theme_PureBiliBili_Splash_Icon3DLight
        "app.bilipai.MainActivitySplashIcon3DDark" -> R.style.Theme_PureBiliBili_Splash_Icon3DDark
        "app.bilipai.MainActivitySplashBiliPai" -> R.style.Theme_PureBiliBili_Splash_BiliPai
        "app.bilipai.MainActivitySplashBiliPaiPink" -> R.style.Theme_PureBiliBili_Splash_BiliPaiPink
        "app.bilipai.MainActivitySplashBiliPaiWhite" -> R.style.Theme_PureBiliBili_Splash_BiliPaiWhite
        "app.bilipai.MainActivitySplashBiliPaiMonet" -> R.style.Theme_PureBiliBili_Splash_BiliPaiMonet
        "app.bilipai.MainActivitySplashNoIcon" -> R.style.Theme_PureBiliBili_Splash_NoIcon
        else -> 0
    }
}

@Suppress("DEPRECATION")
internal fun resolveForwardedLauncherActivityInfo(context: Context, launchIntent: Intent?): ActivityInfo? {
    val component = launchIntent?.getStringExtra(EXTRA_LAUNCHER_COMPONENT)
        ?.let(ComponentName::unflattenFromString)
        ?: return null
    if (component.packageName != context.packageName) return null
    // An icon can be changed while the host is alive, disabling its original alias.
    val info = runCatching {
        context.packageManager.getActivityInfo(component, PackageManager.MATCH_DISABLED_COMPONENTS)
    }.getOrNull() ?: return null
    return info.takeIf {
        resolveSplashThemeResIdForActivityClassName(it.targetActivity ?: it.name) != 0
    }
}

/**
 * Retains the launcher's themed system splash, but never draws a second activity UI.
 * MainActivity alone owns Compose, navigation, playback and home-data preloading.
 */
abstract class MainActivityLauncherEntry : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }
        startActivity(Intent(intent).apply {
            putExtra(
                EXTRA_LAUNCHER_COMPONENT,
                (intent.component ?: componentName).flattenToString()
            )
            setClass(this@MainActivityLauncherEntry, MainActivity::class.java)
        })
        finish()
    }
}

class MainActivitySplashBlueSnowMaid : MainActivityLauncherEntry()
class MainActivitySplashBlueSnowMaidAnnouncement : MainActivityLauncherEntry()
class MainActivitySplashBlueSnowMaidAnnouncementLight : MainActivityLauncherEntry()
class MainActivitySplashBlueSnowMaidAnnouncementDark : MainActivityLauncherEntry()
class MainActivitySplashBlueSnowMaidFront : MainActivityLauncherEntry()
class MainActivitySplashBlueSnowMaidLight : MainActivityLauncherEntry()
class MainActivitySplashBlueSnowMaidDark : MainActivityLauncherEntry()
class MainActivitySplashBlueSnowMaidFrontLight : MainActivityLauncherEntry()
class MainActivitySplashBlueSnowMaidFrontDark : MainActivityLauncherEntry()
class MainActivitySplashIcon3D : MainActivityLauncherEntry()
class MainActivitySplashIcon3DLight : MainActivityLauncherEntry()
class MainActivitySplashIcon3DDark : MainActivityLauncherEntry()
class MainActivitySplashBiliPai : MainActivityLauncherEntry()
class MainActivitySplashBiliPaiPink : MainActivityLauncherEntry()
class MainActivitySplashBiliPaiWhite : MainActivityLauncherEntry()
class MainActivitySplashBiliPaiMonet : MainActivityLauncherEntry()
class MainActivitySplashNoIcon : MainActivityLauncherEntry()
