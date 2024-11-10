package com.shifthackz.aisdv1.data.preference

import android.content.SharedPreferences
import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore
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
import com.shifthackz.android.core.preferences.delegates
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class PreferenceManagerImpl(
    preferences: SharedPreferences,
) : PreferenceManager {

    private val preferencesChangedSubject: BehaviorSubject<Any> =
        BehaviorSubject.createDefault(Unit)

    override var automatic1111ServerUrl: String by preferences.delegates.complexString(
        key = KEY_SERVER_URL,
        default = "",
        serialize = { it.fixUrlSlashes() },
        deserialize = { it.fixUrlSlashes() },
        onChanged = ::onPreferencesChanged,
    )

    override var swarmUiServerUrl: String by preferences.delegates.complexString(
        key = KEY_SWARM_SERVER_URL,
        default = "",
        serialize = { it.fixUrlSlashes() },
        deserialize = { it.fixUrlSlashes() },
        onChanged = ::onPreferencesChanged,
    )

    override var swarmUiModel: String by preferences.delegates.string(
        key = KEY_SWARM_MODEL,
        onChanged = ::onPreferencesChanged,
    )

    override var demoMode: Boolean by preferences.delegates.boolean(
        key = KEY_DEMO_MODE,
        onChanged = ::onPreferencesChanged,
    )

    override var developerMode: Boolean by preferences.delegates.boolean(
        key = KEY_DEVELOPER_MODE,
        onChanged = ::onPreferencesChanged,
    )

    override var localMediaPipeCustomModelPath: String by preferences.delegates.string(
        key = KEY_MEDIA_PIPE_CUSTOM_MODEL_PATH,
        default = LOCAL_DIFFUSION_CUSTOM_PATH,
    )

    override var localOnnxCustomModelPath: String by preferences.delegates.string(
        key = KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH,
        default = LOCAL_DIFFUSION_CUSTOM_PATH,
    )

    override var localOnnxAllowCancel: Boolean by preferences.delegates.boolean(
        key = KEY_ALLOW_LOCAL_DIFFUSION_CANCEL,
        onChanged = ::onPreferencesChanged,
    )

    override var localOnnxSchedulerThread: SchedulersToken by preferences.delegates.complexInt(
        key = KEY_LOCAL_DIFFUSION_SCHEDULER_THREAD,
        default = SchedulersToken.COMPUTATION,
        serialize = { token -> token.ordinal },
        deserialize = { index -> SchedulersToken.entries[index] },
        onChanged = ::onPreferencesChanged,
    )

    override var monitorConnectivity: Boolean by preferences.delegates.complexBoolean(
        key = KEY_MONITOR_CONNECTIVITY,
        default = false,
        serialize = { it },
        deserialize = { if (!source.featureTags.contains(FeatureTag.OwnServer)) false else it },
        onChanged = ::onPreferencesChanged,
    )

    override var autoSaveAiResults: Boolean by preferences.delegates.boolean(
        key = KEY_AI_AUTO_SAVE,
        default = true,
        onChanged = ::onPreferencesChanged,
    )

    override var saveToMediaStore: Boolean by preferences.delegates.boolean(
        key = KEY_SAVE_TO_MEDIA_STORE,
        default = shouldUseNewMediaStore(),
        onChanged = ::onPreferencesChanged,
    )

    override var formAdvancedOptionsAlwaysShow: Boolean by preferences.delegates.boolean(
        key = KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS,
        onChanged = ::onPreferencesChanged,
    )

    override var formPromptTaggedInput: Boolean by preferences.delegates.boolean(
        key = KEY_FORM_PROMPT_TAGGED_INPUT,
        default = false,
        onChanged = ::onPreferencesChanged,
    )

    override var source: ServerSource by preferences.delegates.complexString(
        key = KEY_SERVER_SOURCE,
        default = ServerSource.AUTOMATIC1111,
        serialize = { source -> source.key },
        deserialize = { key -> ServerSource.parse(key) },
        onChanged = ::onPreferencesChanged,
    )

    override var sdModel: String by preferences.delegates.string(
        key = KEY_SD_MODEL,
        onChanged = ::onPreferencesChanged,
    )

    override var hordeApiKey: String by preferences.delegates.string(
        key = KEY_HORDE_API_KEY,
        onChanged = ::onPreferencesChanged,
    )

    override var openAiApiKey: String by preferences.delegates.string(
        key = KEY_OPEN_AI_API_KEY,
        onChanged = ::onPreferencesChanged,
    )

    override var huggingFaceApiKey: String by preferences.delegates.string(
        key = KEY_HUGGING_FACE_API_KEY,
        onChanged = ::onPreferencesChanged,
    )

    override var huggingFaceModel: String by preferences.delegates.string(
        key = KEY_HUGGING_FACE_MODEL_KEY,
        default = HuggingFaceModel.default.alias,
        onChanged = ::onPreferencesChanged,
    )

    override var stabilityAiApiKey: String by preferences.delegates.string(
        key = KEY_STABILITY_AI_API_KEY,
        onChanged = ::onPreferencesChanged,
    )

    override var stabilityAiEngineId: String by preferences.delegates.string(
        key = KEY_STABILITY_AI_ENGINE_ID_KEY,
        onChanged = ::onPreferencesChanged,
    )

    override var onBoardingComplete: Boolean by preferences.delegates.boolean(
        key = KEY_ON_BOARDING_COMPLETE,
    )

    override var forceSetupAfterUpdate: Boolean by preferences.delegates.boolean(
        key = KEY_FORCE_SETUP_AFTER_UPDATE,
        default = true,
        onChanged = ::onPreferencesChanged,
    )

    override var localOnnxModelId: String by preferences.delegates.string(
        key = KEY_LOCAL_MODEL_ID,
        onChanged = ::onPreferencesChanged,
    )

    override var localOnnxUseNNAPI: Boolean by preferences.delegates.boolean(
        key = KEY_LOCAL_NN_API,
        onChanged = ::onPreferencesChanged,
    )

    override var localMediaPipeModelId: String by preferences.delegates.string(
        key = KEY_MEDIA_PIPE_MODEL_ID,
        onChanged = ::onPreferencesChanged,
    )

    override var designUseSystemColorPalette: Boolean by preferences.delegates.boolean(
        key = KEY_DESIGN_DYNAMIC_COLORS,
        onChanged = ::onPreferencesChanged,
    )

    override var designUseSystemDarkTheme: Boolean by preferences.delegates.boolean(
        key = KEY_DESIGN_SYSTEM_DARK_THEME,
        default = true,
        onChanged = ::onPreferencesChanged,
    )

    override var designDarkTheme: Boolean by preferences.delegates.boolean(
        key = KEY_DESIGN_DARK_THEME,
        default = true,
        onChanged = ::onPreferencesChanged,
    )

    override var designColorToken: String by preferences.delegates.string(
        key = KEY_DESIGN_COLOR_TOKEN,
        default = "${ColorToken.MAUVE}",
        onChanged = ::onPreferencesChanged,
    )

    override var designDarkThemeToken: String by preferences.delegates.string(
        key = KEY_DESIGN_DARK_TOKEN,
        default = "${DarkThemeToken.FRAPPE}",
        onChanged = ::onPreferencesChanged,
    )
    override var backgroundGeneration: Boolean by preferences.delegates.boolean(
        key = KEY_BACKGROUND_GENERATION,
        onChanged = ::onPreferencesChanged,
    )

    override var backgroundProcessCount: Int by preferences.delegates.int(
        key = KEY_BACKGROUND_PROCESS_COUNT,
        default = 0,
    )

    override var galleryGrid: Grid by preferences.delegates.complexInt(
        key = KEY_GALLERY_GRID,
        default = Grid.entries.first(),
        serialize = { grid -> grid.ordinal },
        deserialize = { index -> Grid.entries[index] },
        onChanged = ::onPreferencesChanged,
    )

    override fun observe(): Flowable<Settings> = preferencesChangedSubject
        .toFlowable(BackpressureStrategy.LATEST)
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
            )
        }

    override fun refresh(): Completable = Completable.fromAction {
        preferencesChangedSubject.onNext(Unit)
    }

    private fun <T> onPreferencesChanged(value: T) = preferencesChangedSubject.onNext(value)

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
    }
}
