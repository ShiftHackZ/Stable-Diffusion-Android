package com.shifthackz.aisdv1.data.preference

import android.content.SharedPreferences
import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class PreferenceManagerImpl(
    private val preferences: SharedPreferences,
) : PreferenceManager {

    private val preferencesChangedSubject: BehaviorSubject<Unit> =
        BehaviorSubject.createDefault(Unit)

    override var serverUrl: String
        get() = (if (useSdAiCloud) "" else preferences.getString(KEY_SERVER_URL, "")
            ?: "").fixUrlSlashes()
        set(value) = preferences.edit()
            .putString(KEY_SERVER_URL, (if (useSdAiCloud) "" else value).fixUrlSlashes())
            .apply()
            .also { onPreferencesChanged() }

    override var demoMode: Boolean
        get() = if (useSdAiCloud) false
        else preferences.getBoolean(KEY_DEMO_MODE, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_DEMO_MODE, value)
            .apply()
            .also { onPreferencesChanged() }

    override val useSdAiCloud: Boolean
        get() = source == ServerSource.SDAI
//        set(value) = preferences.edit()
//            .putBoolean(KEY_SD_AI_CLOUD_MODE, value)
//            .apply()
//            .also { onPreferencesChanged() }

    override var monitorConnectivity: Boolean
        get() = if (source != ServerSource.CUSTOM) false
        else preferences.getBoolean(KEY_MONITOR_CONNECTIVITY, true)
        set(value) = preferences.edit()
            .putBoolean(KEY_MONITOR_CONNECTIVITY, value)
            .apply()
            .also { onPreferencesChanged() }

    override var autoSaveAiResults: Boolean
        get() = preferences.getBoolean(KEY_AI_AUTO_SAVE, true)
        set(value) = preferences.edit()
            .putBoolean(KEY_AI_AUTO_SAVE, value)
            .apply()
            .also { onPreferencesChanged() }

    override var formAdvancedOptionsAlwaysShow: Boolean
        get() = preferences.getBoolean(KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS, value)
            .apply()
            .also { onPreferencesChanged() }
    override var source: ServerSource
        get() = (preferences.getString(KEY_SERVER_SOURCE, ServerSource.CUSTOM.key) ?: ServerSource.CUSTOM.key)
            .let(ServerSource.Companion::parse)
        set(value) = preferences.edit()
            .putString(KEY_SERVER_SOURCE, value.key)
            .apply()
            .also { onPreferencesChanged() }
    override var hordeApiKey: String
        get() = preferences.getString(KEY_HORDE_API_KEY, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_HORDE_API_KEY, value)
            .apply()
            .also { onPreferencesChanged() }

    override fun observe(): Flowable<Settings> = preferencesChangedSubject
        .toFlowable(BackpressureStrategy.LATEST)
        .map {
            Settings(
                serverUrl = serverUrl,
                demoMode = demoMode,
                useSdAiCloud = useSdAiCloud,
                monitorConnectivity = monitorConnectivity,
                autoSaveAiResults = autoSaveAiResults,
                formAdvancedOptionsAlwaysShow = formAdvancedOptionsAlwaysShow,
                source = source,
                hordeApiKey = hordeApiKey,
            )
        }

    private fun onPreferencesChanged() = preferencesChangedSubject.onNext(Unit)

    companion object {
        private const val KEY_SERVER_URL = "key_server_url"
        private const val KEY_DEMO_MODE = "key_demo_mode"
        private const val KEY_MONITOR_CONNECTIVITY = "key_monitor_connectivity"
        private const val KEY_AI_AUTO_SAVE = "key_ai_auto_save"
        private const val KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS = "key_always_show_advanced_options"
        private const val KEY_SERVER_SOURCE = "key_server_source"
        private const val KEY_HORDE_API_KEY = "key_horde_api_key"
    }
}
