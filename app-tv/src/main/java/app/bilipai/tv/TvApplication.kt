package app.bilipai.tv

import android.app.Application
import app.bilipai.core.network.CoreNetworkConfig
import app.bilipai.core.network.NetworkModule
import app.bilipai.core.network.WbiKeyManager
import app.bilipai.core.store.TokenManager

class TvApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkModule.init(this, CoreNetworkConfig(isPrivacyModeEnabled = { TvPreferences(it).privacyMode }))
        TokenManager.init(this)
        WbiKeyManager.restoreFromStorage(this)
    }
}
