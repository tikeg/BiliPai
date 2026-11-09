package app.bilipai.navigation

import app.bilipai.feature.home.components.BottomNavItem
import app.bilipai.navigation3.BiliPaiNavEntryContentRole
import app.bilipai.navigation3.BiliPaiNavKey
import app.bilipai.navigation3.legacyRouteToBiliPaiNavKey
import app.bilipai.navigation3.resolveBiliPaiNavEntryContentRole
import app.bilipai.navigation3.toLegacyRoute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import app.bilipai.core.navigation.ScreenRoutes

class ListenVideoNavigationPolicyTest {

    @Test
    fun `listen video route round trips and owns top level role`() {
        assertEquals(ScreenRoutes.ListenVideo.route, BiliPaiNavKey.ListenVideo.toLegacyRoute())
        assertEquals(
            BiliPaiNavKey.ListenVideo,
            legacyRouteToBiliPaiNavKey(ScreenRoutes.ListenVideo.route)
        )
        assertEquals(
            BiliPaiNavEntryContentRole.LISTEN_VIDEO,
            resolveBiliPaiNavEntryContentRole(BiliPaiNavKey.ListenVideo)
        )
        assertEquals(ScreenRoutes.ListenVideo.route, BottomNavItem.LISTEN_VIDEO.route)
    }

    @Test
    fun `listen video is a visible bottom pager destination`() {
        val visibleItems = resolveVisibleBottomBarItems(
            listOf("HOME", "DYNAMIC", "HISTORY", "LISTEN_VIDEO", "PROFILE")
        )

        assertEquals(BottomNavItem.LISTEN_VIDEO, visibleItems[3])
        assertEquals(
            3,
            resolveBottomPagerPageForRoute(ScreenRoutes.ListenVideo.route, visibleItems)
        )
        assertTrue(shouldBypassNavigationDebounceForRoute(ScreenRoutes.ListenVideo.route))
    }
}
