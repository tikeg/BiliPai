package app.bilipai.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.bilipai.core.store.BackToTopSettingsStore
import app.bilipai.core.store.DEFAULT_BACK_TO_TOP_BUTTON_ENABLED

@Composable
fun rememberBackToTopButtonEnabled(): Boolean {
    val context = LocalContext.current
    val preferenceFlow = remember(context) {
        BackToTopSettingsStore.isEnabled(context)
    }
    val enabled by preferenceFlow.collectAsStateWithLifecycle(
        initialValue = DEFAULT_BACK_TO_TOP_BUTTON_ENABLED,
    )
    return enabled
}
