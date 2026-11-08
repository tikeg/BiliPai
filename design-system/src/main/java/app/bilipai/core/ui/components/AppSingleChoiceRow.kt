package app.bilipai.core.ui.components

import app.bilipai.core.ui.AppChromeSizeTokens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.graphics.Shape
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.AppSpacingTokens
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.ContainerLevel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected

/**
 * Theme-native single-choice row.
 *
 * Selection is expressed by [AppRadioButton], which delegates to Miuix or Material 3.
 * The row deliberately keeps one semantic container in both states so feature code never
 * has to hand-pair a selected background with its content colors.
 */
@Composable
fun AppSingleChoiceRow(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AppShapes.container(ContainerLevel.Chip),
    content: @Composable RowScope.() -> Unit,
) {
    val feedbackClick = appActionWithFeedback(app.bilipai.core.ui.AppHapticScene.MENUS, onClick)
    if (app.bilipai.core.ui.isMiuixNonGlassEnabled()) {
        top.yukonga.miuix.kmp.basic.BasicComponent(
            modifier = modifier.semantics {
                this.selected = selected
                if (!enabled) disabled()
            },
            onClick = feedbackClick,
            enabled = enabled,
            role = androidx.compose.ui.semantics.Role.RadioButton,
            endActions = { AppRadioButton(selected = selected, onClick = null, enabled = enabled) },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, content = content)
        }
        return
    }
    AppSurface(
        onClick = feedbackClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        color = AppSurfaceTokens.surfaceContainerHigh(),
        contentColor = AppSurfaceTokens.onSurfaceContainerHigh(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AppChromeSizeTokens.CompactRowMinHeightDp.dp)
                .padding(horizontal = AppSpacingTokens.Medium, vertical = AppSpacingTokens.Small),
            horizontalArrangement = Arrangement.spacedBy(AppSpacingTokens.Small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
            AppRadioButton(
                selected = selected,
                onClick = null,
                enabled = enabled,
            )
        }
    }
}
