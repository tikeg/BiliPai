package app.bilipai.data.repository

import app.bilipai.data.model.response.CodecInfo
import app.bilipai.data.model.response.FormatInfo
import app.bilipai.data.model.response.LiveDurl
import app.bilipai.data.model.response.LivePlayUrlData
import app.bilipai.data.model.response.LiveQuality
import app.bilipai.data.model.response.Playurl
import app.bilipai.data.model.response.PlayurlInfo
import app.bilipai.data.model.response.StreamInfo
import app.bilipai.data.model.response.UrlInfo
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LivePlaybackUrlPolicyTest {

    @Test
    fun `quality descriptions without stream urls are not playable`() {
        assertFalse(
            hasPlayableLiveUrl(
                LivePlayUrlData(
                    quality_description = listOf(LiveQuality(qn = 10000, desc = "原画"))
                )
            )
        )
    }

    @Test
    fun `xlive host and base url form a playable stream`() {
        assertTrue(
            hasPlayableLiveUrl(
                LivePlayUrlData(
                    playurl_info = PlayurlInfo(
                        playurl = Playurl(
                            stream = listOf(
                                StreamInfo(
                                    protocolName = "http_hls",
                                    format = listOf(
                                        FormatInfo(
                                            formatName = "fmp4",
                                            codec = listOf(
                                                CodecInfo(
                                                    baseUrl = "/live/index.m4s",
                                                    url_info = listOf(
                                                        UrlInfo(host = "https://example.com")
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `legacy blank urls are ignored`() {
        assertFalse(
            hasPlayableLiveUrl(
                LivePlayUrlData(durl = listOf(LiveDurl(url = "")))
            )
        )
        assertTrue(
            hasPlayableLiveUrl(
                LivePlayUrlData(durl = listOf(LiveDurl(url = "https://example.com/live.flv")))
            )
        )
    }
}
