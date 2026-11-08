package app.bilipai.feature.dynamic

import app.bilipai.data.model.response.DynamicItem
import app.bilipai.data.model.response.DynamicModules
import app.bilipai.data.model.response.DynamicStatModule
import app.bilipai.data.model.response.StatItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class DynamicForwardCountPolicyTest {

    @Test
    fun applyDynamicForwardCountIncrement_updatesOnlyTheConfirmedDynamic() {
        val original = DynamicItem(
            id_str = "100",
            modules = DynamicModules(
                module_stat = DynamicStatModule(forward = StatItem(count = 9))
            )
        )
        val untouched = DynamicItem(id_str = "200")

        val updated = applyDynamicForwardCountIncrement(
            items = listOf(original, untouched),
            dynamicId = "100"
        )

        assertNotSame(original, updated.first())
        assertEquals(9, original.modules.module_stat?.forward?.count)
        assertEquals(10, updated.first().modules.module_stat?.forward?.count)
        assertEquals(untouched, updated.last())
    }
}
