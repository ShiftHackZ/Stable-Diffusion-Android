package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.core.common.file.LOCAL_DIFFUSION_CUSTOM_PATH
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.FeatureTag
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class PreferenceManagerImpl(
    private val keyValueStore: KeyValueStore,
) : PreferenceManager {

    private val preferencesChangedState = MutableStateFlow(Any())

    override var automatic1111ServerUrl: String
        get() = keyValueStore.getString(KEY_SERVER_URL).fixUrlSlashes()
        set(value) = putString(KEY_SERVER_URL, value.fixUrlSlashes())

    override var swarmUiServerUrl: String
        get() = keyValueStore.getString(KEY_SWARM_SERVER_URL).fixUrlSlashes()
        set(value) = putString(KEY_SWARM_SERVER_URL, value.fixUrlSlashes())

    override var swarmUiModel: String
        get() = keyValueStore.getString(KEY_SWARM_MODEL)
        set(value) = putString(KEY_SWARM_MODEL, value)

    override var demoMode: Boolean
        get() = keyValueStore.getBoolean(KEY_DEMO_MODE)
        set(value) = putBoolean(KEY_DEMO_MODE, value)

    override var developerMode: Boolean
        get() = keyValueStore.getBoolean(KEY_DEVELOPER_MODE)
        set(value) = putBoolean(KEY_DEVELOPER_MODE, value)

    override var localMediaPipeCustomModelPath: String
        get() = keyValueStore.getString(KEY_MEDIA_PIPE_CUSTOM_MODEL_PATH, LOCAL_DIFFUSION_CUSTOM_PATH)
        set(value) = keyValueStore.putString(KEY_MEDIA_PIPE_CUSTOM_MODEL_PATH, value)

    override var localOnnxCustomModelPath: String
        get() = keyValueStore.getString(KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH, LOCAL_DIFFUSION_CUSTOM_PATH)
        set(value) = keyValueStore.putString(KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH, value)

    override var localOnnxAllowCancel: Boolean
        get() = keyValueStore.getBoolean(KEY_ALLOW_LOCAL_DIFFUSION_CANCEL)
        set(value) = putBoolean(KEY_ALLOW_LOCAL_DIFFUSION_CANCEL, value)

    override var localOnnxSchedulerThread: SchedulersToken
        get() = enumValueOrDefault(
            index = keyValueStore.getInt(KEY_LOCAL_DIFFUSION_SCHEDULER_THREAD, SchedulersToken.COMPUTATION.ordinal),
            default = SchedulersToken.COMPUTATION,
        )
        set(value) = putInt(KEY_LOCAL_DIFFUSION_SCHEDULER_THREAD, value.ordinal)

    override var monitorConnectivity: Boolean
        get() {
            val value = keyValueStore.getBoolean(KEY_MONITOR_CONNECTIVITY)
            return if (!source.featureTags.contains(FeatureTag.OwnServer)) false else value
        }
        set(value) = putBoolean(KEY_MONITOR_CONNECTIVITY, value)

    override var autoSaveAiResults: Boolean
        get() = keyValueStore.getBoolean(KEY_AI_AUTO_SAVE, true)
        set(value) = putBoolean(KEY_AI_AUTO_SAVE, value)

    override var saveToMediaStore: Boolean
        get() = keyValueStore.getBoolean(KEY_SAVE_TO_MEDIA_STORE, defaultSaveToMediaStore())
        set(value) = putBoolean(KEY_SAVE_TO_MEDIA_STORE, value)

    override var formAdvancedOptionsAlwaysShow: Boolean
        get() = keyValueStore.getBoolean(KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS)
        set(value) = putBoolean(KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS, value)

    override var formPromptTaggedInput: Boolean
        get() = keyValueStore.getBoolean(KEY_FORM_PROMPT_TAGGED_INPUT)
        set(value) = putBoolean(KEY_FORM_PROMPT_TAGGED_INPUT, value)

    override var source: ServerSource
        get() = ServerSource.parse(keyValueStore.getString(KEY_SERVER_SOURCE, ServerSource.AUTOMATIC1111.key))
        set(value) = putString(KEY_SERVER_SOURCE, value.key)

    override var sdModel: String
        get() = keyValueStore.getString(KEY_SD_MODEL)
        set(value) = putString(KEY_SD_MODEL, value)

    override var hordeApiKey: String
        get() = keyValueStore.getString(KEY_HORDE_API_KEY)
        set(value) = putString(KEY_HORDE_API_KEY, value)

    override var openAiApiKey: String
        get() = keyValueStore.getString(KEY_OPEN_AI_API_KEY)
        set(value) = putString(KEY_OPEN_AI_API_KEY, value)

    override var huggingFaceApiKey: String
        get() = keyValueStore.getString(KEY_HUGGING_FACE_API_KEY)
        set(value) = putString(KEY_HUGGING_FACE_API_KEY, value)

    override var huggingFaceModel: String
        get() = keyValueStore.getString(KEY_HUGGING_FACE_MODEL_KEY, HuggingFaceModel.default.alias)
        set(value) = putString(KEY_HUGGING_FACE_MODEL_KEY, value)

    override var stabilityAiApiKey: String
        get() = keyValueStore.getString(KEY_STABILITY_AI_API_KEY)
        set(value) = putString(KEY_STABILITY_AI_API_KEY, value)

    override var stabilityAiEngineId: String
        get() = keyValueStore.getString(KEY_STABILITY_AI_ENGINE_ID_KEY)
        set(value) = putString(KEY_STABILITY_AI_ENGINE_ID_KEY, value)

    override var onBoardingComplete: Boolean
        get() = keyValueStore.getBoolean(KEY_ON_BOARDING_COMPLETE)
        set(value) = keyValueStore.putBoolean(KEY_ON_BOARDING_COMPLETE, value)

    override var forceSetupAfterUpdate: Boolean
        get() = keyValueStore.getBoolean(KEY_FORCE_SETUP_AFTER_UPDATE, true)
        set(value) = putBoolean(KEY_FORCE_SETUP_AFTER_UPDATE, value)

    override var localOnnxModelId: String
        get() = keyValueStore.getString(KEY_LOCAL_MODEL_ID)
        set(value) = putString(KEY_LOCAL_MODEL_ID, value)

    override var localOnnxUseNNAPI: Boolean
        get() = keyValueStore.getBoolean(KEY_LOCAL_NN_API)
        set(value) = putBoolean(KEY_LOCAL_NN_API, value)

    override var localMediaPipeModelId: String
        get() = keyValueStore.getString(KEY_MEDIA_PIPE_MODEL_ID)
        set(value) = putString(KEY_MEDIA_PIPE_MODEL_ID, value)

    override var designUseSystemColorPalette: Boolean
        get() = keyValueStore.getBoolean(KEY_DESIGN_DYNAMIC_COLORS)
        set(value) = putBoolean(KEY_DESIGN_DYNAMIC_COLORS, value)

    override var designUseSystemDarkTheme: Boolean
        get() = keyValueStore.getBoolean(KEY_DESIGN_SYSTEM_DARK_THEME, true)
        set(value) = putBoolean(KEY_DESIGN_SYSTEM_DARK_THEME, value)

    override var designDarkTheme: Boolean
        get() = keyValueStore.getBoolean(KEY_DESIGN_DARK_THEME, true)
        set(value) = putBoolean(KEY_DESIGN_DARK_THEME, value)

    override var designColorToken: String
        get() = keyValueStore.getString(KEY_DESIGN_COLOR_TOKEN, "${ColorToken.MAUVE}")
        set(value) = putString(KEY_DESIGN_COLOR_TOKEN, value)

    override var designDarkThemeToken: String
        get() = keyValueStore.getString(KEY_DESIGN_DARK_TOKEN, "${DarkThemeToken.FRAPPE}")
        set(value) = putString(KEY_DESIGN_DARK_TOKEN, value)

    override var backgroundGeneration: Boolean
        get() = keyValueStore.getBoolean(KEY_BACKGROUND_GENERATION)
        set(value) = putBoolean(KEY_BACKGROUND_GENERATION, value)

    override var backgroundProcessCount: Int
        get() = keyValueStore.getInt(KEY_BACKGROUND_PROCESS_COUNT)
        set(value) = keyValueStore.putInt(KEY_BACKGROUND_PROCESS_COUNT, value)

    override var galleryGrid: Grid
        get() = enumValueOrDefault(
            index = keyValueStore.getInt(KEY_GALLERY_GRID, Grid.entries.first().ordinal),
            default = Grid.entries.first(),
        )
        set(value) = putInt(KEY_GALLERY_GRID, value.ordinal)

    override var languageCode: String
        get() = keyValueStore.getString(KEY_LANGUAGE_CODE)
        set(value) = putString(KEY_LANGUAGE_CODE, value)

    override fun observe(): Flow<Settings> = preferencesChangedState
        .map {
            Settings(
                serverUrl = automatic1111ServerUrl,
                sdModel = sdModel,
                demoMode = demoMode,
                developerMode = developerMode,
                localDiffusionAllowCancel = localOnnxAllowCancel,
                localDiffusionSchedulerThread = localOnnxSchedulerThread,
                monitorConnectivity = monitorConnectivity,
                backgroundGeneration = backgroundGeneration,
                autoSaveAiResults = autoSaveAiResults,
                saveToMediaStore = saveToMediaStore,
                formAdvancedOptionsAlwaysShow = formAdvancedOptionsAlwaysShow,
                formPromptTaggedInput = formPromptTaggedInput,
                source = source,
                hordeApiKey = hordeApiKey,
                localUseNNAPI = localOnnxUseNNAPI,
                designUseSystemColorPalette = designUseSystemColorPalette,
                designUseSystemDarkTheme = designUseSystemDarkTheme,
                designDarkTheme = designDarkTheme,
                designColorToken = designColorToken,
                designDarkThemeToken = designDarkThemeToken,
                galleryGrid = galleryGrid,
                languageCode = languageCode,
            )
        }

    override suspend fun refresh() {
        preferencesChangedState.value = Any()
    }

    private fun putString(key: String, value: String) {
        keyValueStore.putString(key, value)
        onPreferencesChanged()
    }

    private fun putBoolean(key: String, value: Boolean) {
        keyValueStore.putBoolean(key, value)
        onPreferencesChanged()
    }

    private fun putInt(key: String, value: Int) {
        keyValueStore.putInt(key, value)
        onPreferencesChanged()
    }

    private fun onPreferencesChanged() {
        preferencesChangedState.value = Any()
    }

    private inline fun <reified T : Enum<T>> enumValueOrDefault(index: Int, default: T): T =
        enumValues<T>().getOrNull(index) ?: default

    companion object {
        const val KEY_SERVER_URL = "key_server_url"
        const val KEY_SWARM_SERVER_URL = "key_swarm_server_url"
        const val KEY_SWARM_MODEL = "key_swarm_model"
        const val KEY_DEMO_MODE = "key_demo_mode"
        const val KEY_DEVELOPER_MODE = "key_developer_mode"
        const val KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH = "key_local_diffusion_custom_model_path"
        const val KEY_MEDIA_PIPE_CUSTOM_MODEL_PATH = "key_mediapipe_custom_model_path"
        const val KEY_ALLOW_LOCAL_DIFFUSION_CANCEL = "key_allow_local_diffusion_cancel"
        const val KEY_LOCAL_DIFFUSION_SCHEDULER_THREAD = "key_local_diffusion_scheduler_thread"
        const val KEY_MONITOR_CONNECTIVITY = "key_monitor_connection"
        const val KEY_AI_AUTO_SAVE = "key_ai_auto_save"
        const val KEY_SAVE_TO_MEDIA_STORE = "key_save_to_media_store"
        const val KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS = "key_always_show_advanced_options"
        const val KEY_FORM_PROMPT_TAGGED_INPUT = "key_prompt_tagged_input_kb"
        const val KEY_SERVER_SOURCE = "key_server_source"
        const val KEY_SD_MODEL = "key_sd_model"
        const val KEY_HORDE_API_KEY = "key_horde_api_key"
        const val KEY_OPEN_AI_API_KEY = "key_open_ai_api_key"
        const val KEY_HUGGING_FACE_API_KEY = "key_hugging_face_api_key"
        const val KEY_HUGGING_FACE_MODEL_KEY = "key_hugging_face_model_key"
        const val KEY_STABILITY_AI_API_KEY = "key_stability_ai_api_key"
        const val KEY_STABILITY_AI_ENGINE_ID_KEY = "key_stability_ai_engine_id_key"
        const val KEY_ON_BOARDING_COMPLETE = "key_on_boarding_complete"
        const val KEY_FORCE_SETUP_AFTER_UPDATE = "force_upd_setup_v0.x.x-v0.6.2"
        const val KEY_MEDIA_PIPE_MODEL_ID = "key_mediapipe_model_id"
        const val KEY_LOCAL_MODEL_ID = "key_local_model_id"
        const val KEY_LOCAL_NN_API = "key_local_nn_api"
        const val KEY_DESIGN_DYNAMIC_COLORS = "key_design_dynamic_colors"
        const val KEY_DESIGN_SYSTEM_DARK_THEME = "key_design_system_dark_theme"
        const val KEY_DESIGN_DARK_THEME = "key_design_dark_theme"
        const val KEY_DESIGN_COLOR_TOKEN = "key_design_color_token_theme"
        const val KEY_DESIGN_DARK_TOKEN = "key_design_dark_color_token_theme"
        const val KEY_BACKGROUND_GENERATION = "key_background_generation"
        const val KEY_BACKGROUND_PROCESS_COUNT = "key_background_process_count"
        const val KEY_GALLERY_GRID = "key_gallery_grid"
        const val KEY_LANGUAGE_CODE = "key_language_code"
    }
}

internal expect fun defaultSaveToMediaStore(): Boolean
