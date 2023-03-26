package com.shifthackz.aisdv1.data.preference

import android.content.SharedPreferences
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

class PreferenceManagerImpl(
    private val preferences: SharedPreferences,
) : PreferenceManager {

    override var serverUrl: String
        get() = if (useSdAiCloud) ""
        else preferences.getString(KEY_SERVER_URL, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_SERVER_URL, if (useSdAiCloud) "" else value)
            .apply()

    override var demoMode: Boolean
        get() = preferences.getBoolean(KEY_DEMO_MODE, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_DEMO_MODE, value)
            .apply()

    override var useSdAiCloud: Boolean
        get() = preferences.getBoolean(KEY_SD_AI_CLOUD_MODE, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_SD_AI_CLOUD_MODE, value)
            .apply()

    override var monitorConnectivity: Boolean
        get() = if (useSdAiCloud) false
        else preferences.getBoolean(KEY_MONITOR_CONNECTIVITY, true)
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
        private const val KEY_DEMO_MODE = "key_demo_mode"
        private const val KEY_SD_AI_CLOUD_MODE = "key_sd_ai_cloud_mode"
        private const val KEY_MONITOR_CONNECTIVITY = "key_monitor_connectivity"
        private const val KEY_AI_AUTO_SAVE = "key_ai_auto_save"
    }
}
