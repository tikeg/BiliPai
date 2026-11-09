package app.bilipai.feature.list

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import app.bilipai.core.navigation.ScreenRoutes

class CommonListHistoryUpNavigationStructureTest {

    @Test
    fun `video card exposes optional up click callback`() {
        val source = loadSource("app/src/main/java/app/bilipai/feature/home/components/cards/VideoCard.kt")

        assertTrue(source.contains("onUpClick: ((Long) -> Unit)? = null"))
    }

    @Test
    fun `common list passes optional up click callback into video cards`() {
        val source = loadSource("app/src/main/java/app/bilipai/feature/list/CommonListScreen.kt")

        assertTrue(source.contains("onUpClick: ((Long) -> Unit)? = null"))
        assertTrue(source.contains("onUpClick = onUpClick"))
    }

    @Test
    fun `history route connects up click to space screen`() {
        val source = loadSource("app/src/main/java/app/bilipai/navigation/AppNavigation.kt")

        assertTrue(source.contains("onUpClick = { mid -> pushNavigation3Route(ScreenRoutes.Space.createRoute(mid)) }"))
    }

    @Test
    fun `favorite and season series routes connect up click to space screen`() {
        val navigationSource = loadSource("app/src/main/java/app/bilipai/navigation/AppNavigation.kt")
        val listSource = loadSource("app/src/main/java/app/bilipai/feature/list/CommonListScreen.kt")

        val favoriteBranch = navigationSource
            .substringAfter("BiliPaiNavEntryContentRole.FAVORITE")
            .substringBefore("BiliPaiNavEntryContentRole.LIKED_VIDEOS")
        val seasonBranch = navigationSource
            .substringAfter("BiliPaiNavEntryContentRole.SEASON_SERIES_DETAIL")
            .substringBefore("BiliPaiNavEntryContentRole.BANGUMI")

        assertTrue(favoriteBranch.contains("onUpClick = { mid ->"))
        assertTrue(favoriteBranch.contains("ScreenRoutes.Space.createRoute(mid)"))
        assertTrue(seasonBranch.contains("onUpClick = { mid ->"))
        assertTrue(seasonBranch.contains("ScreenRoutes.Space.createRoute(mid)"))
        assertTrue(listSource.contains("onUpClick = if (!isHistoryBatchMode)"))
    }

    private fun loadSource(path: String): String {
        val candidates = listOf(
            File(path),
            File("../$path")
        )
        return candidates.first { it.exists() }.readText()
    }
}
