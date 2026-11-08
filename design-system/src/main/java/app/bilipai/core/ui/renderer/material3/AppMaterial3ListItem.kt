package app.bilipai.core.ui.renderer.material3

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun AppMaterial3ListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier,
    overlineContent: (@Composable () -> Unit)?,
    supportingContent: (@Composable () -> Unit)?,
    leadingContent: (@Composable () -> Unit)?,
    trailingContent: (@Composable () -> Unit)?,
) = ListItem(
    colors = ListItemDefaults.colors(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        headlineColor = app.bilipai.core.ui.AppContentColors.primaryText(),
        supportingColor = app.bilipai.core.ui.AppContentColors.secondaryText(),
        overlineColor = app.bilipai.core.ui.AppContentColors.secondaryText(),
        trailingIconColor = app.bilipai.core.ui.AppContentColors.secondaryText(),
    ),
    headlineContent = headlineContent,
    modifier = modifier,
    overlineContent = overlineContent,
    supportingContent = supportingContent,
    leadingContent = leadingContent,
    trailingContent = trailingContent,
)
