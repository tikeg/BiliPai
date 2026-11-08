package app.bilipai.core.util

import app.bilipai.navigation3.BiliPaiNavKey
import app.bilipai.navigation3.legacyRouteToBiliPaiNavKey
import app.bilipai.navigation3.toLegacyRoute
import app.bilipai.resolveMainActivityLinkNavigation
import app.bilipai.navigation.MessageLinkNavigationAction
import app.bilipai.navigation.resolveMessageLinkNavigationAction
import app.bilipai.navigation.SearchSubmitAction
import app.bilipai.navigation.resolveSearchSubmitAction
import app.bilipai.feature.web.WebViewNavigationAction
import app.bilipai.feature.web.resolveWebViewNavigationAction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ArticleCollectionNavigationTest {
    @Test
    fun `web app and plain collection links resolve without becoming article ids`() {
        listOf(
            "https://www.bilibili.com/read/readlist/rl123",
            "https://www.bilibili.com/read/mobile-readlist/rl123?from=av170001",
            "https://m.bilibili.com/read/mobile-readlist?id=123",
            "bilibili://read/readlist/rl123",
            "bilibili://readlist/123",
            "RL123",
        ).forEach { url ->
            assertEquals(BilibiliNavigationTarget.ArticleCollection(123), BilibiliNavigationTargetParser.parse(url), url)
        }
        assertEquals(BilibiliNavigationTarget.Article(123), BilibiliNavigationTargetParser.parse("https://www.bilibili.com/read/cv123"))
    }

    @Test
    fun `invalid collection identifiers do not fall through to article query`() {
        listOf(
            "https://www.bilibili.com/read/readlist/rl0?id=55",
            "https://www.bilibili.com/read/readlist/rl-1?id=55",
            "https://www.bilibili.com/read/readlist/rl99999999999999999999999",
            "https://example.com/read/readlist/rl123",
        ).forEach { assertNull(BilibiliNavigationTargetParser.parse(it), it) }
    }

    @Test
    fun `collection navigation preserves title and reading marker through legacy route`() {
        val key = BiliPaiNavKey.ArticleCollection(123, "文集 / 阅读 & 收藏", 456)
        assertEquals(key, legacyRouteToBiliPaiNavKey(key.toLegacyRoute()))
    }
    @Test
    fun `search message and external entry preserve native collection destination`() {
        val url = "https://www.bilibili.com/read/readlist/rl123"
        val target = BilibiliNavigationTarget.ArticleCollection(123)
        assertEquals(SearchSubmitAction.OpenNativeTarget(target), resolveSearchSubmitAction("rl123"))
        assertEquals(MessageLinkNavigationAction.ArticleCollection(123), resolveMessageLinkNavigationAction(url))
        assertEquals(BiliPaiNavKey.ArticleCollection(123),
            legacyRouteToBiliPaiNavKey(resolveMainActivityLinkNavigation(target)?.pendingNavigationRoute))
        assertEquals(WebViewNavigationAction.DispatchTarget(target), resolveWebViewNavigationAction(url, true))
        assertEquals(WebViewNavigationAction.AllowWebLoad, resolveWebViewNavigationAction(url, false))
    }

}
