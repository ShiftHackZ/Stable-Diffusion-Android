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

    companion object {
        private const val KEY_SERVER_URL = "key_server_url"
    }
}
