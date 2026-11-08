package app.bilipai.feature.home

internal fun resolveHomeDrawerLogoutAction(
    onLogout: (() -> Unit)?,
    onProfileClick: () -> Unit
): () -> Unit {
    return onLogout ?: onProfileClick
}
