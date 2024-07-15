package com.shifthackz.aisdv1.data.preference

import android.content.SharedPreferences
import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
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
        get() = (preferences.getString(KEY_SERVER_URL, "") ?: "").fixUrlSlashes()
        set(value) = preferences.edit()
            .putString(KEY_SERVER_URL, value.fixUrlSlashes())
            .apply()
            .also { onPreferencesChanged() }

    override var demoMode: Boolean
        get() = preferences.getBoolean(KEY_DEMO_MODE, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_DEMO_MODE, value)
            .apply()
            .also { onPreferencesChanged() }

    override var monitorConnectivity: Boolean
        get() = if (source != ServerSource.AUTOMATIC1111) false
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

    override var saveToMediaStore: Boolean
        get() = preferences.getBoolean(KEY_SAVE_TO_MEDIA_STORE, shouldUseNewMediaStore())
        set(value) = preferences.edit()
            .putBoolean(KEY_SAVE_TO_MEDIA_STORE, value)
            .apply()
            .also { onPreferencesChanged() }

    override var formAdvancedOptionsAlwaysShow: Boolean
        get() = preferences.getBoolean(KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS, value)
            .apply()
            .also { onPreferencesChanged() }

    override var formPromptTaggedInput: Boolean
        get() = preferences.getBoolean(KEY_FORM_PROMPT_TAGGED_INPUT, true)
        set(value) = preferences.edit()
            .putBoolean(KEY_FORM_PROMPT_TAGGED_INPUT, value)
            .apply()
            .also { onPreferencesChanged() }

    override var source: ServerSource
        get() = (preferences.getString(KEY_SERVER_SOURCE, ServerSource.AUTOMATIC1111.key) ?: ServerSource.AUTOMATIC1111.key)
            .let(ServerSource.Companion::parse)
        set(value) = preferences.edit()
            .putString(KEY_SERVER_SOURCE, value.key)
            .apply()
            .also { onPreferencesChanged() }

    override var sdModel: String
        get() = preferences.getString(KEY_SD_MODEL, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_SD_MODEL, value)
            .apply()
            .also { onPreferencesChanged() }

    override var hordeApiKey: String
        get() = preferences.getString(KEY_HORDE_API_KEY, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_HORDE_API_KEY, value)
            .apply()
            .also { onPreferencesChanged() }

    override var openAiApiKey: String
        get() = preferences.getString(KEY_OPEN_AI_API_KEY, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_OPEN_AI_API_KEY, value)
            .apply()
            .also { onPreferencesChanged() }

    override var huggingFaceApiKey: String
        get() = preferences.getString(KEY_HUGGING_FACE_API_KEY, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_HUGGING_FACE_API_KEY, value)
            .apply()
            .also { onPreferencesChanged() }

    override var huggingFaceModel: String
        get() {
            return preferences.getString(
                KEY_HUGGING_FACE_MODEL_KEY,
                HuggingFaceModel.default.alias,
            ) ?: HuggingFaceModel.default.alias
        }
        set(value) = preferences.edit()
            .putString(KEY_HUGGING_FACE_MODEL_KEY, value)
            .apply()
            .also { onPreferencesChanged() }

    override var stabilityAiApiKey: String
        get() = preferences.getString(KEY_STABILITY_AI_API_KEY, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_STABILITY_AI_API_KEY, value)
            .apply()
            .also { onPreferencesChanged() }

    override var stabilityAiEngineId: String
        get() = preferences.getString(KEY_STABILITY_AI_ENGINE_ID_KEY, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_STABILITY_AI_ENGINE_ID_KEY, value)
            .apply()
            .also { onPreferencesChanged() }

    override var forceSetupAfterUpdate: Boolean
        get() = preferences.getBoolean(KEY_FORCE_SETUP_AFTER_UPDATE, true)
        set(value) = preferences.edit()
            .putBoolean(KEY_FORCE_SETUP_AFTER_UPDATE, value)
            .apply()
            .also { onPreferencesChanged() }

    override var localModelId: String
        get() = preferences.getString(KEY_LOCAL_MODEL_ID, "") ?: ""
        set(value) = preferences.edit()
            .putString(KEY_LOCAL_MODEL_ID, value)
            .apply()
            .also { onPreferencesChanged() }

    override var localUseNNAPI: Boolean
        get() = preferences.getBoolean(KEY_LOCAL_NN_API, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_LOCAL_NN_API, value)
            .apply()
            .also { onPreferencesChanged() }

    override var designUseSystemColorPalette: Boolean
        get() = preferences.getBoolean(KEY_DESIGN_DYNAMIC_COLORS, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_DESIGN_DYNAMIC_COLORS, value)
            .apply()
            .also { onPreferencesChanged() }

    override var designUseSystemDarkTheme: Boolean
        get() = preferences.getBoolean(KEY_DESIGN_SYSTEM_DARK_THEME, true)
        set(value) = preferences.edit()
            .putBoolean(KEY_DESIGN_SYSTEM_DARK_THEME, value)
            .apply()
            .also { onPreferencesChanged() }

    override var designDarkTheme: Boolean
        get() = preferences.getBoolean(KEY_DESIGN_DARK_THEME, true)
        set(value) = preferences.edit()
            .putBoolean(KEY_DESIGN_DARK_THEME, value)
            .apply()
            .also { onPreferencesChanged() }

    override var designColorToken: String
        get() = preferences.getString(KEY_DESIGN_COLOR_TOKEN, "${ColorToken.MAUVE}") ?: "${ColorToken.MAUVE}"
        set(value) = preferences.edit()
            .putString(KEY_DESIGN_COLOR_TOKEN, value)
            .apply()
            .also { onPreferencesChanged() }

    override var designDarkThemeToken: String
        get() =  preferences.getString(KEY_DESIGN_DARK_TOKEN, "${DarkThemeToken.FRAPPE}") ?: "${DarkThemeToken.FRAPPE}"
        set(value) = preferences.edit()
            .putString(KEY_DESIGN_DARK_TOKEN, value)
            .apply()
            .also { onPreferencesChanged() }

    override fun observe(): Flowable<Settings> = preferencesChangedSubject
        .toFlowable(BackpressureStrategy.LATEST)
        .distinctUntilChanged()
        .map {
            Settings(
                serverUrl = serverUrl,
                sdModel = sdModel,
                demoMode = demoMode,
                monitorConnectivity = monitorConnectivity,
                autoSaveAiResults = autoSaveAiResults,
                saveToMediaStore = saveToMediaStore,
                formAdvancedOptionsAlwaysShow = formAdvancedOptionsAlwaysShow,
                formPromptTaggedInput = formPromptTaggedInput,
                source = source,
                hordeApiKey = hordeApiKey,
                localUseNNAPI = localUseNNAPI,
                designUseSystemColorPalette = designUseSystemColorPalette,
                designUseSystemDarkTheme = designUseSystemDarkTheme,
                designDarkTheme = designDarkTheme,
                designColorToken = designColorToken,
                designDarkThemeToken = designDarkThemeToken,
            )
        }

    private fun onPreferencesChanged() = preferencesChangedSubject.onNext(Unit)

    companion object {
        const val KEY_SERVER_URL = "key_server_url"
        const val KEY_DEMO_MODE = "key_demo_mode"
        const val KEY_MONITOR_CONNECTIVITY = "key_monitor_connectivity"
        const val KEY_AI_AUTO_SAVE = "key_ai_auto_save"
        const val KEY_SAVE_TO_MEDIA_STORE = "key_save_to_media_store"
        const val KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS = "key_always_show_advanced_options"
        const val KEY_FORM_PROMPT_TAGGED_INPUT = "key_prompt_tagged_input"
        const val KEY_SERVER_SOURCE = "key_server_source"
        const val KEY_SD_MODEL = "key_sd_model"
        const val KEY_HORDE_API_KEY = "key_horde_api_key"
        private const val KEY_OPEN_AI_API_KEY = "key_open_ai_api_key"
        private const val KEY_HUGGING_FACE_API_KEY = "key_hugging_face_api_key"
        private const val KEY_HUGGING_FACE_MODEL_KEY = "key_hugging_face_model_key"
        private const val KEY_STABILITY_AI_API_KEY = "key_stability_ai_api_key"
        private const val KEY_STABILITY_AI_ENGINE_ID_KEY = "key_stability_ai_engine_id_key"
        private const val KEY_LOCAL_NN_API = "key_local_nn_api"
        private const val KEY_LOCAL_MODEL_ID = "key_local_model_id"
        private const val KEY_DESIGN_DYNAMIC_COLORS = "key_design_dynamic_colors"
        private const val KEY_DESIGN_SYSTEM_DARK_THEME = "key_design_system_dark_theme"
        private const val KEY_DESIGN_DARK_THEME = "key_design_dark_theme"
        private const val KEY_DESIGN_COLOR_TOKEN = "key_design_color_token_theme"
        private const val KEY_DESIGN_DARK_TOKEN = "key_design_dark_color_token_theme"
        private const val KEY_FORCE_SETUP_AFTER_UPDATE = "force_upd_setup_v0.x.x-v0.5.8"
    }
}
