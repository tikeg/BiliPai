package app.bilipai.core.util

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PermissionHelperStructureTest {

    @Test
    fun permissionDialogsUseAppAlertChromeAndThemePrimary() {
        val source = File(
            "src/main/java/app/bilipai/core/util/PermissionHelper.kt"
        ).readText()
        assertTrue(source.contains("AppAlertDialog("))
        assertTrue(source.contains("AppButton("))
        assertTrue(source.contains("AppTextButton("))
        assertTrue(source.contains("MaterialTheme.colorScheme.primary"))
        assertFalse(source.contains("BiliPink"))
        assertFalse(source.contains("AlertDialog("))
    }
}
