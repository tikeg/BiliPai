package app.bilipai.core.store

import android.content.Context
import app.bilipai.core.theme.AppUiStyle

suspend fun applyOnboardingRecommendedUiStyle(context: Context) {
    SettingsManager.setUiStyle(context, AppUiStyle.MATERIAL3)
}
