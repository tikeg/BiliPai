package app.bilipai.feature.settings

object AppUpdateAutoCheckGate {
    @Volatile
    private var checkedInProcess = false

    fun tryMarkChecked(): Boolean = synchronized(this) {
        if (checkedInProcess) {
            false
        } else {
            checkedInProcess = true
            true
        }
    }
}
