package app.bilipai.feature.settings

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class MiuixThemeFacadeStructureTest {

    @Test
    fun featureLayer_avoidsDirectMiuixThemeColorSchemeReads() {
        val offenders = listOf(
            "app/src/main/java/app/bilipai/feature/search/SearchScreen.kt",
            "app/src/main/java/app/bilipai/feature/home/components/TopBar.kt",
            "app/src/main/java/app/bilipai/feature/home/components/BottomBar.kt",
            "app/src/main/java/app/bilipai/feature/message/InboxScreen.kt",
            "app/src/main/java/app/bilipai/feature/message/feed/MessageFeedCommon.kt",
            "app/src/main/java/app/bilipai/feature/video/ui/section/AiSummarySection.kt",
            "app/src/main/java/app/bilipai/feature/video/ui/section/VideoNoteSection.kt",
            "app/src/main/java/app/bilipai/feature/video/ui/components/VideoSettingsPanel.kt",
            "app/src/main/java/app/bilipai/feature/settings/AppSegmentedControl.kt",
            "app/src/main/java/app/bilipai/navigation3/BiliPaiNavDisplayHost.kt",
        ).filter { path ->
            loadSource(path).contains("MiuixTheme.colorScheme")
        }
        assertTrue(
            offenders.isEmpty(),
            "Direct MiuixTheme.colorScheme reads should route through AppSurfaceTokens:\n" +
                offenders.joinToString("\n")
        )
    }

    private fun loadSource(path: String): String {
        val normalizedPath = path.removePrefix("app/")
        val sourceFile = listOf(
            File(path),
            File("../$path"),
            File(normalizedPath)
        ).firstOrNull { it.exists() }
        require(sourceFile != null) { "Cannot locate $path from ${File(".").absolutePath}" }
        return sourceFile.readText()
    }
}
