package app.bilipai.feature.home

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.width
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.AppThemeConfig
import app.bilipai.core.ui.ProvideAppThemeConfig
import app.bilipai.core.ui.components.AppGlassMenuPresenter
import app.bilipai.core.ui.components.AppGlassMenuRequest
import app.bilipai.core.ui.components.LocalAppGlassMenuPresenter
import app.bilipai.core.ui.components.LocalPageImageLoadingAllowed
import app.bilipai.data.model.response.Owner
import app.bilipai.data.model.response.VideoItem
import app.bilipai.feature.home.components.cards.ElegantVideoCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import top.yukonga.miuix.kmp.theme.MiuixTheme

class HomeVideoCardMenuTest {
    @get:Rule val rule = createComposeRule()

    @Test fun materialMenuDispatchesAndReopens() = checkNativeMenu(AppUiStyle.MATERIAL3)
    @Test fun miuixMenuDispatchesAndReopens() = checkNativeMenu(AppUiStyle.MIUIX)

    private fun checkNativeMenu(style: AppUiStyle) {
        var watchLater = 0
        var dismissed = 0
        var navigated = 0
        rule.setContent {
            CompositionLocalProvider(
                LocalAppUiStyle provides style,
                LocalPageImageLoadingAllowed provides false,
            ) {
                ProvideAppThemeConfig(AppThemeConfig(liquidGlassEnabled = false)) {
                    MiuixTheme {
                        MaterialTheme {
                            Box(Modifier.width(240.dp).testTag("menu_card")) {
                                ElegantVideoCard(
                                    video = VideoItem(bvid = "BV_menu", title = "菜单测试", owner = Owner(name = "UP")),
                                    index = 0,
                                    animationEnabled = false,
                                    transitionEnabled = false,
                                    onWatchLater = { watchLater++ },
                                    onDismiss = { dismissed++ },
                                    onClick = { _, _ -> navigated++ },
                                )
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("稍后再看").assertDoesNotExist()
        val card = rule.onNodeWithTag("menu_card").getUnclippedBoundsInRoot()
        val overflow = rule.onNodeWithContentDescription("更多操作").getUnclippedBoundsInRoot()
        assertTrue(overflow.left.value > card.left.value + card.width.value / 2f)
        rule.onNodeWithContentDescription("更多操作").performClick()
        rule.onNodeWithText("稍后再看").performClick()
        rule.runOnIdle { assertEquals(1, watchLater) }
        rule.onNodeWithText("稍后再看").assertDoesNotExist()
        rule.onNodeWithContentDescription("更多操作").performClick()
        rule.onNodeWithText("不感兴趣").performClick()
        rule.runOnIdle {
            assertEquals(1, dismissed)
            assertEquals(0, navigated)
        }
    }

    @Test
    fun glassMenuRetainsTheButtonSourceAcrossDismissAndReopen() {
        val requests = mutableListOf<AppGlassMenuRequest>()
        rule.setContent {
            val view = LocalView.current
            val presenter = remember(view) {
                object : AppGlassMenuPresenter {
                    override val ownerView: View = view
                    override fun show(owner: Any, request: AppGlassMenuRequest) { requests.add(request) }
                    override fun dismiss(owner: Any) = Unit
                }
            }
            CompositionLocalProvider(
                LocalPageImageLoadingAllowed provides false,
                LocalAppGlassMenuPresenter provides presenter,
            ) {
                ProvideAppThemeConfig(AppThemeConfig(liquidGlassEnabled = true)) {
                    MaterialTheme {
                        Box(Modifier.width(240.dp)) {
                            ElegantVideoCard(
                                video = VideoItem(bvid = "BV_glass_menu", title = "菜单测试", owner = Owner(name = "UP")),
                                index = 0,
                                animationEnabled = false,
                                transitionEnabled = false,
                                onDismiss = {},
                                onClick = { _, _ -> },
                            )
                        }
                    }
                }
            }
        }
        rule.runOnIdle { assertEquals(0, requests.size) }
        rule.onNodeWithContentDescription("更多操作").performClick()
        rule.waitUntil { requests.size == 1 }
        rule.runOnIdle {
            assertNotNull(requests.last().sourceLayer)
            assertNotNull(requests.last().anchor())
            requests.last().onDismissRequest()
        }
        rule.waitForIdle()
        rule.onNodeWithContentDescription("更多操作").performClick()
        rule.waitUntil { requests.size == 2 }
        rule.runOnIdle { assertNotNull(requests.last().sourceLayer) }
    }
}
