package app.bilipai.feature.dynamic.components

import app.bilipai.data.model.response.ArchiveMajor
import app.bilipai.data.model.response.DynamicMajorBadge
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DynamicArchiveBadgePolicyTest {

    @Test
    fun archiveBadgePrefersDesktopBadgeText() {
        assertEquals(
            "充电专属",
            resolveDynamicArchiveBadgeLabel(
                ArchiveMajor(
                    badge = DynamicMajorBadge(text = "充电专属"),
                    isChargingArc = true
                )
            )
        )
    }

    @Test
    fun archiveChargeFieldsFallbackToChargeBadge() {
        assertEquals(
            "充电专属",
            resolveDynamicArchiveBadgeLabel(
                ArchiveMajor(elecArcType = 1)
            )
        )
    }

    @Test
    fun regularArchiveDoesNotShowBadge() {
        assertNull(resolveDynamicArchiveBadgeLabel(ArchiveMajor()))
    }
}
