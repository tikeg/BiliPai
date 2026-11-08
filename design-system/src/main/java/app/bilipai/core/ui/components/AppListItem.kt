package app.bilipai.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.renderer.material3.AppMaterial3ListItem
import app.bilipai.core.ui.renderer.miuix.AppMiuixListItem

@Composable
fun AppListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> AppMaterial3ListItem(
            headlineContent = headlineContent,
            modifier = modifier,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
        )
        AppUiStyle.MIUIX -> AppMiuixListItem(
            headlineContent = headlineContent,
            modifier = modifier,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
        )
    }
}
