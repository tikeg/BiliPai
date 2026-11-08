@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package app.bilipai.core.ui.renderer.material3

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import app.bilipai.core.ui.components.LocalAppIconButtonGlassSurfaceProvided
import app.bilipai.core.ui.components.AppIconButtonColors
import app.bilipai.core.ui.components.AppIconButtonVariant
import app.bilipai.core.ui.components.LocalAppActionContentColor
import app.bilipai.core.ui.components.appDesktopInteractionVisuals

@Composable
internal fun AppMaterial3IconButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    colors: AppIconButtonColors?,
    interactionSource: MutableInteractionSource?,
    variant: AppIconButtonVariant,
    content: @Composable () -> Unit,
) {
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val glassSurfaceProvided = LocalAppIconButtonGlassSurfaceProvided.current
    val nativeShape = if (glassSurfaceProvided) CircleShape else when (variant) {
        AppIconButtonVariant.Standard -> IconButtonDefaults.standardShape
        AppIconButtonVariant.Filled -> IconButtonDefaults.filledShape
    }
    val neutralContentColor = LocalAppActionContentColor.current.takeOrElse {
        app.bilipai.core.ui.currentAppContentColor()
    }
    val defaultColors = when (variant) {
        AppIconButtonVariant.Standard -> IconButtonDefaults.iconButtonColors(
            contentColor = neutralContentColor,
            disabledContentColor = neutralContentColor.copy(alpha = 0.38f),
        )
        AppIconButtonVariant.Filled -> IconButtonDefaults.filledIconButtonColors(
            containerColor = app.bilipai.core.ui.AppContentColors.accent(),
            contentColor = app.bilipai.core.ui.AppContentColors.onAccent(),
        )
    }
    val requestedColors = colors?.let {
        defaultColors.copy(
            containerColor = it.containerColor,
            contentColor = it.contentColor,
            disabledContainerColor = it.disabledContainerColor,
            disabledContentColor = it.disabledContentColor,
        )
    } ?: defaultColors
    val nativeColors = if (glassSurfaceProvided) {
        requestedColors.copy(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )
    } else requestedColors
    val nativeModifier = modifier.appDesktopInteractionVisuals(
        interactionSource = resolvedInteractionSource,
        enabled = enabled,
        shape = nativeShape,
    )

    when (variant) {
        AppIconButtonVariant.Standard -> IconButton(
            onClick = onClick,
            modifier = nativeModifier,
            enabled = enabled,
            colors = nativeColors,
            interactionSource = resolvedInteractionSource,
            shape = nativeShape,
            content = {
                app.bilipai.core.ui.ProvideAppButtonSlotPalette(
                    containerColor = if (enabled) nativeColors.containerColor else nativeColors.disabledContainerColor,
                    enabled = enabled,
                    content = content,
                )
            },
        )
        AppIconButtonVariant.Filled -> FilledIconButton(
            onClick = onClick,
            modifier = nativeModifier,
            enabled = enabled,
            shape = nativeShape,
            colors = nativeColors,
            interactionSource = resolvedInteractionSource,
            content = {
                app.bilipai.core.ui.ProvideAppButtonSlotPalette(
                    containerColor = if (enabled) nativeColors.containerColor else nativeColors.disabledContainerColor,
                    enabled = enabled,
                    content = content,
                )
            },
        )
    }
}
