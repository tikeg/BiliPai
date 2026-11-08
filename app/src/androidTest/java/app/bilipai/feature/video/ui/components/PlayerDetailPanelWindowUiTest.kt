package app.bilipai.feature.video.ui.components

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.AppPopupWindowHostRenderer
import app.bilipai.core.ui.AppThemeConfig
import app.bilipai.core.ui.LocalAppPopupWindowHostRenderer
import app.bilipai.core.ui.LocalAppThemeConfig
import app.bilipai.core.ui.LocalComponentMotionEnabled
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

class PlayerDetailPanelWindowUiTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun portraitPanelCoversWindowBottomWhileListAvoidsNavigationInset() {
        rule.runOnUiThread { rule.activity.enableEdgeToEdge() }
        val glass = mutableStateOf(false)
        val style = mutableStateOf(AppUiStyle.MATERIAL3)
        var portrait = false
        var windowBottom = Float.NaN
        var safeBottom = Float.NaN
        var panelBottom = Float.NaN
        var contentBottom = Float.NaN
        val renderer = object : AppPopupWindowHostRenderer {
            @Composable
            override fun Render(content: @Composable () -> Unit) {
                Box(Modifier.fillMaxSize()) { content() }
            }
        }
        rule.setContent {
            val window = LocalWindowInfo.current.containerSize
            val safe = playerPanelSafeWindowBounds()
            portrait = window.height > window.width
            CompositionLocalProvider(
                LocalAppThemeConfig provides AppThemeConfig(liquidGlassEnabled = glass.value),
                LocalAppUiStyle provides style.value,
                LocalAppPopupWindowHostRenderer provides renderer,
                LocalComponentMotionEnabled provides false,
            ) {
                MaterialTheme {
                    Box(Modifier.fillMaxSize().onGloballyPositioned {
                        val origin = it.positionOnScreen().y
                        windowBottom = origin + window.height
                        safeBottom = origin + safe.bottom
                    }) {
                        PlayerDetailPanel(
                            title = "分P", isFullscreen = false, playerBounds = null,
                            compactHeight = true, onDismiss = {},
                            modifier = Modifier.onGloballyPositioned {
                                panelBottom = it.positionOnScreen().y + it.size.height
                            },
                        ) { _ ->
                            Box(Modifier.fillMaxSize().onGloballyPositioned {
                                contentBottom = it.positionOnScreen().y + it.size.height
                            })
                        }
                    }
                }
            }
        }
        rule.waitForIdle()
        assumeTrue(portrait)
        for (uiStyle in AppUiStyle.entries) {
            for (useGlass in listOf(false, true)) {
                rule.runOnIdle { style.value = uiStyle; glass.value = useGlass }
                rule.waitForIdle()
                rule.runOnIdle {
                    assertTrue(panelBottom.isFinite() && windowBottom.isFinite())
                    assertEquals("Panel must cover the underlying page to the window edge", windowBottom, panelBottom, 2f)
                    assertEquals("List must avoid the navigation area exactly once", safeBottom, contentBottom, 2f)
                }
            }
        }
    }
}
