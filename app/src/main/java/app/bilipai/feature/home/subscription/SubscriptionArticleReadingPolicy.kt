package app.bilipai.feature.home.subscription

import app.bilipai.core.plugin.feed.isHttpFeedUrl

internal fun buildSubscriptionArticleShareText(title: String, sourceTitle: String, link: String): String =
    listOf(title, sourceTitle, link.takeIf(::isHttpFeedUrl).orEmpty())
        .map { it.trim() }.filter { it.isNotBlank() }.joinToString("\n")
