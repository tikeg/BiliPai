package app.bilipai.feature.plugin.googlecast

import app.bilipai.plugin.sdk.CastDiscoveryRequirement
import org.junit.Assert.assertEquals
import org.junit.Test

class GoogleCastPluginTest {

    @Test
    fun `app owned discovery requires local network permission starting with android 17`() {
        assertEquals(CastDiscoveryRequirement.NONE, resolveGoogleCastDiscoveryRequirement(36))
        assertEquals(CastDiscoveryRequirement.RAW_LOCAL_NETWORK, resolveGoogleCastDiscoveryRequirement(37))
        assertEquals(CastDiscoveryRequirement.RAW_LOCAL_NETWORK, resolveGoogleCastDiscoveryRequirement(38))
    }

}
