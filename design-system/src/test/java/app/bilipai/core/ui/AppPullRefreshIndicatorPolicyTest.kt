package app.bilipai.core.ui

import app.bilipai.core.theme.AppUiStyle
import kotlin.test.Test
import kotlin.test.assertEquals

class AppPullRefreshIndicatorPolicyTest {

    @Test
    fun refreshIndicatorRenderer_branchesByUiStyle() {
        assertEquals(
            AppPullRefreshIndicatorRenderer.MIUIX,
            resolveAppPullRefreshIndicatorRenderer(AppUiStyle.MIUIX),
        )
        assertEquals(
            AppPullRefreshIndicatorRenderer.MATERIAL3,
            resolveAppPullRefreshIndicatorRenderer(AppUiStyle.MATERIAL3),
        )
    }
}
