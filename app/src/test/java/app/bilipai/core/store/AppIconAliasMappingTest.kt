package app.bilipai.core.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppIconAliasMappingTest {

    @Test
    fun resolveAppIconLauncherAlias_supportsCanonicalAndLegacyKeys() {
        val packageName = "app.bilipai"

        assertEquals(
            "app.bilipai.MainActivityAliasBlueSnowMaid",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBlueSnowMaidFront",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid_front")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBlueSnowMaidAnnouncement",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid_announcement")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBiliPai",
            resolveAppIconLauncherAlias(packageName, "icon_bilipai")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBiliPai",
            resolveAppIconLauncherAlias(packageName, "BiliPai")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBiliPaiPink",
            resolveAppIconLauncherAlias(packageName, "icon_bilipai_pink")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBiliPaiWhite",
            resolveAppIconLauncherAlias(packageName, "BiliPai White")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBiliPaiMonet",
            resolveAppIconLauncherAlias(packageName, "BiliPai Monet")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBlueSnowMaid",
            resolveAppIconLauncherAlias(packageName, "icon_headphone")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBlueSnowMaid",
            resolveAppIconLauncherAlias(packageName, "unknown")
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBiliPaiNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_bilipai", splashIconVisible = false)
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBlueSnowMaidNoIcon",
            resolveAppIconLauncherAlias(packageName, "unknown", splashIconVisible = false)
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBlueSnowMaidFrontNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid_front", splashIconVisible = false)
        )
        assertEquals(
            "app.bilipai.MainActivityAliasBlueSnowMaidAnnouncementNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid_announcement", splashIconVisible = false)
        )
    }

    @Test
    fun resolveAppIconLauncherAlias_supportsFixedMaidAppearances() {
        val packageName = "app.bilipai"

        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidLight",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid",
                appearance = AppIconAppearance.LIGHT
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidDarkNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid",
                splashIconVisible = false,
                appearance = AppIconAppearance.DARK
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidFrontDark",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid_front",
                appearance = AppIconAppearance.DARK
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidFrontLightNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid_front",
                splashIconVisible = false,
                appearance = AppIconAppearance.LIGHT
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidAnnouncementLight",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid_announcement",
                appearance = AppIconAppearance.LIGHT
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidAnnouncementDarkNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid_announcement",
                splashIconVisible = false,
                appearance = AppIconAppearance.DARK
            )
        )
    }

    @Test
    fun playWindowAppearanceSelectsItsLauncherWithAndWithoutSplashIcon() {
        val packageName = "app.bilipai"
        val entries = listOf(
            Triple(AppIconAppearance.FOLLOW_SYSTEM, "MainActivityAlias3DLauncher", "MainActivityAlias3DNoIcon"),
            Triple(AppIconAppearance.LIGHT, "MainActivityAlias3DLight", "MainActivityAlias3DLightNoIcon"),
            Triple(AppIconAppearance.DARK, "MainActivityAlias3DDark", "MainActivityAlias3DDarkNoIcon"),
        )
        val managed = allManagedAppIconLauncherAliases(packageName)
        for ((appearance, withSplash, withoutSplash) in entries) {
            assertEquals("$packageName.$withSplash", resolveAppIconLauncherAlias(packageName, "icon_3d", appearance = appearance))
            assertEquals("$packageName.$withoutSplash", resolveAppIconLauncherAlias(packageName, "icon_3d", splashIconVisible = false, appearance = appearance))
            assertTrue("$packageName.$withSplash" in managed)
            assertTrue("$packageName.$withoutSplash" in managed)
        }
    }

    @Test
    fun resolveAppIconLauncherAlias_ignoresAppearanceForFixedPaletteIcons() {
        assertEquals(
            "app.bilipai.MainActivityAliasBiliPai",
            resolveAppIconLauncherAlias(
                "app.bilipai",
                "icon_bilipai",
                appearance = AppIconAppearance.DARK
            )
        )
    }

    @Test
    fun resolveAppIconLauncherAlias_keepsStableComponentNamespaceForDebugBuilds() {
        assertEquals(
            "app.bilipai.MainActivityAlias3DLauncher",
            resolveAppIconLauncherAlias("app.bilipai.debug", "icon_3d")
        )
    }

    @Test
    fun allManagedAppIconLauncherAliases_containsBiliPaiAndHeadphone_withoutRemovedAliases() {
        val aliases = allManagedAppIconLauncherAliases("app.bilipai")
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaid"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidNoIcon"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidAnnouncement"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidAnnouncementNoIcon"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidAnnouncementLight"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidAnnouncementDarkNoIcon"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidFront"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidFrontNoIcon"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidLight"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidDarkNoIcon"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidFrontLightNoIcon"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBlueSnowMaidFrontDark"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBiliPai"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBiliPaiPink"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBiliPaiWhite"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBiliPaiMonet"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasHeadphone"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAlias3D"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAliasBiliPaiNoIcon"))
        assertTrue(aliases.contains("app.bilipai.MainActivityAlias3DNoIcon"))
        kotlin.test.assertFalse(aliases.contains("app.bilipai.MainActivityAliasBlue"))
        kotlin.test.assertFalse(aliases.contains("app.bilipai.MainActivityAliasNeon"))
        kotlin.test.assertFalse(aliases.contains("app.bilipai.MainActivityAliasTelegramBlueCoin"))
        kotlin.test.assertFalse(aliases.contains("app.bilipai.MainActivityAliasPink"))
        kotlin.test.assertFalse(aliases.contains("app.bilipai.MainActivityAliasPurple"))
        kotlin.test.assertFalse(aliases.contains("app.bilipai.MainActivityAliasGreen"))
        kotlin.test.assertFalse(aliases.contains("app.bilipai.MainActivityAliasFlatMaterial"))
        kotlin.test.assertFalse(aliases.contains("app.bilipai.MainActivityAliasRetro"))
    }
}
