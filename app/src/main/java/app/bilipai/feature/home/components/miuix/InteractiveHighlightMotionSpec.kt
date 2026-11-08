package app.bilipai.feature.home.components.miuix

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import app.bilipai.core.ui.components.appPressHighlightPressSpec
import app.bilipai.core.ui.components.appPressHighlightPositionSpec

internal fun interactiveHighlightPressSpec(): SpringSpec<Float> = appPressHighlightPressSpec()

internal fun interactiveHighlightPositionSpec(): SpringSpec<Offset> = appPressHighlightPositionSpec()
