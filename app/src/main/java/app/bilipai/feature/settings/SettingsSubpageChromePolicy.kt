package app.bilipai.feature.settings

import app.bilipai.core.ui.AppContentColors

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.bilipai.core.ui.AppSurfaceTokens

/**
 * Shared chrome for settings sub-pages so top bars match the parent settings
 * screen and each other across iOS / Material 3 / Miuix presets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun settingsSubpageTopAppBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = AppSurfaceTokens.groupedListContainer(),
    titleContentColor = AppContentColors.primaryText(),
    navigationIconContentColor = AppContentColors.primaryText(),
    actionIconContentColor = AppContentColors.primaryText()
)

@Composable
fun settingsSubpageContainerColor(): Color = AppSurfaceTokens.groupedListContainer()