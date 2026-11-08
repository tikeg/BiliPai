package app.bilipai.feature.video.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.ContainerLevel

/**
 * Semantic shapes for the video detail surface.
 *
 * Material 3 uses its native shape scale so detail cards and controls keep the
 * expressive curvature expected by the preset. Miuix retains the existing app
 * container tokens and is therefore unaffected by Material-specific tuning.
 */
object VideoDetailShapes {

    @Composable
    fun contentCard(): Shape = when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> MaterialTheme.shapes.large
        AppUiStyle.MIUIX -> AppShapes.container(ContainerLevel.Card)
    }

    @Composable
    fun media(): Shape = when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> MaterialTheme.shapes.medium
        AppUiStyle.MIUIX -> AppShapes.container(ContainerLevel.Field)
    }

    @Composable
    fun field(): Shape = when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> MaterialTheme.shapes.medium
        AppUiStyle.MIUIX -> AppShapes.container(ContainerLevel.Field)
    }

    @Composable
    fun leadingIcon(): Shape = when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> MaterialTheme.shapes.medium
        AppUiStyle.MIUIX -> AppShapes.container(ContainerLevel.Chip)
    }

    @Composable
    fun compactIcon(): Shape = when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> MaterialTheme.shapes.small
        AppUiStyle.MIUIX -> AppShapes.container(ContainerLevel.Chip)
    }

    @Composable
    fun action(): Shape = when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> MaterialTheme.shapes.extraLarge
        AppUiStyle.MIUIX -> AppShapes.container(ContainerLevel.Card)
    }
}
