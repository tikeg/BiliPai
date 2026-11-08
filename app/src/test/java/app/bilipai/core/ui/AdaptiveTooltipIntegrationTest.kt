package app.bilipai.core.ui

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class AdaptiveTooltipIntegrationTest {

    @Test
    fun appearanceDescriptionCardAndAdaptiveTooltipWireOfficialPath() {
        val tooltipSource = load(
            "design-system/src/main/java/app/bilipai/core/ui/AdaptiveTooltip.kt"
        )
        val appearanceSource = load(
            "app/src/main/java/app/bilipai/feature/settings/screen/AppearanceSettingsScreen.kt"
        )

        assertTrue(tooltipSource.contains("MiuixTooltipBox("))
        assertTrue(tooltipSource.contains("rememberPresetPrimitiveRenderer()"))
        assertTrue(appearanceSource.contains("AdaptivePlainTooltipBox("))
    }

    private fun load(path: String): String = listOf(
        File(path),
        File("../$path"),
        File(path.removePrefix("app/")),
    ).first { it.exists() }.readText()
}
