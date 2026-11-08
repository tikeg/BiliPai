package app.bilipai.feature.home

import app.bilipai.data.model.response.VideoItem
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeWatchLaterPolicyTest {

    @Test
    fun resolveWatchLaterAid_prefersAidWhenAvailable() {
        val video = VideoItem(id = 123L, aid = 456L)

        assertEquals(456L, resolveWatchLaterAid(video))
    }

    @Test
    fun resolveWatchLaterAid_fallsBackToIdWhenAidMissing() {
        val video = VideoItem(id = 123L, aid = 0L)

        assertEquals(123L, resolveWatchLaterAid(video))
    }
}

