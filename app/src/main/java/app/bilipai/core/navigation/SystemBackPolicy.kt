package app.bilipai.core.navigation

internal enum class AppSystemBackAction {
    RETURN_TO_HOME_TAB,
    NAVIGATE_UP,
    FINISH_ACTIVITY
}

internal fun shouldInterceptSystemBackForAppAction(
    action: AppSystemBackAction
): Boolean {
    return action == AppSystemBackAction.RETURN_TO_HOME_TAB
}
