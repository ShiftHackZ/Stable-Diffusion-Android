package com.shifthackz.aisdv1.data.preference

import android.content.SharedPreferences
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

class PreferenceManagerImpl(
    private val preferences: SharedPreferences,
) : PreferenceManager {

    override var serverUrl: String
        get() = preferences.getString(KEY_SERVER_URL, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_SERVER_URL, value)
            .apply()

    override var monitorConnectivity: Boolean
        get() = preferences.getBoolean(KEY_MONITOR_CONNECTIVITY, true)
        set(value) = preferences.edit()
            .putBoolean(KEY_MONITOR_CONNECTIVITY, value)
            .apply()

    override var autoSaveAiResults: Boolean
        get() = preferences.getBoolean(KEY_AI_AUTO_SAVE, true)
        set(value) = preferences.edit()
            .putBoolean(KEY_AI_AUTO_SAVE, value)
            .apply()

    companion object {
        private const val KEY_SERVER_URL = "key_server_url"
        private const val KEY_MONITOR_CONNECTIVITY = "key_monitor_connectivity"
        private const val KEY_AI_AUTO_SAVE = "key_ai_auto_save"
    }
}
