package app.bilipai.core.ui

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppChromeSizeTokenAdoptionTest {

    @Test
    fun `first batch capsule chrome surfaces read shared size tokens`() {
        val tokenizedSources = listOf(
            "app/src/main/java/app/bilipai/feature/home/components/TopTabStylePolicy.kt",
            "app/src/main/java/app/bilipai/feature/search/SearchScreen.kt",
            "app/src/main/java/app/bilipai/feature/video/screen/VideoContentSection.kt",
            "app/src/main/java/app/bilipai/feature/video/ui/components/CommentSortFilterBar.kt",
            "app/src/main/java/app/bilipai/feature/live/LiveBiliPaiVisualPolicy.kt",
            "app/src/main/java/app/bilipai/feature/live/LiveHomeCategoryIndicatorPolicy.kt",
            "app/src/main/java/app/bilipai/feature/space/SpaceTabChromePolicy.kt",
            "app/src/main/java/app/bilipai/feature/list/CommonListAppearancePolicy.kt"
        )

        tokenizedSources.forEach { path ->
            val source = loadSource(path)
            assertTrue(
                source.contains("resolveCompactCapsuleChromeSpec(") ||
                    source.contains("compactChromeSpec") ||
                    source.contains("CompactCapsuleChromeSpec") ||
                    source.contains("AppChromeSizeTokens"),
                "$path should use shared compact capsule chrome tokens"
            )
        }
    }

    @Test
    fun `first batch segmented chrome no longer keeps oversized local heights`() {
        val segmentedSources = listOf(
            "app/src/main/java/app/bilipai/feature/live/LiveBiliPaiVisualPolicy.kt",
            "app/src/main/java/app/bilipai/feature/live/LiveHomeCategoryIndicatorPolicy.kt",
            "app/src/main/java/app/bilipai/feature/space/SpaceTabChromePolicy.kt"
        )

        segmentedSources.forEach { path ->
            val source = loadSource(path)
            assertFalse(source.contains("heightDp = 58"), "$path should not keep the old 58dp capsule height")
            assertFalse(source.contains("indicatorHeightDp = 56"), "$path should not keep the old 56dp indicator height")
        }
    }

    private fun loadSource(path: String): String {
        val normalizedPath = path.removePrefix("app/")
        val sourceFile = listOf(
            File(path),
            File(normalizedPath)
        ).firstOrNull { it.exists() }
        require(sourceFile != null) { "Cannot locate $path from ${File(".").absolutePath}" }
        return sourceFile.readText()
    }
}
