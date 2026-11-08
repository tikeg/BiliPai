package app.bilipai.core.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.renderer.material3.AppMaterial3Badge
import app.bilipai.core.ui.renderer.miuix.AppMiuixBadge

@Composable
fun AppBadge(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    val scheme = androidx.compose.material3.MaterialTheme.colorScheme
    val resolvedContainer = if (containerColor == Color.Unspecified) scheme.error else containerColor
    val preferredContent = if (contentColor == Color.Unspecified) {
        androidx.compose.material3.contentColorFor(resolvedContainer)
    } else contentColor
    val resolvedContent = app.bilipai.core.ui.resolveButtonContentColor(
        resolvedContainer, preferredContent, scheme,
    )
    when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> AppMaterial3Badge(
            modifier = modifier,
            containerColor = resolvedContainer,
            contentColor = resolvedContent,
            content = content,
        )
        AppUiStyle.MIUIX -> AppMiuixBadge(
            modifier = modifier,
            containerColor = resolvedContainer,
            contentColor = resolvedContent,
            content = content,
        )
    }
}
