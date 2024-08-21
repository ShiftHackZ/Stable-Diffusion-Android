package com.shifthackz.aisdv1.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_AI_AUTO_SAVE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_ALLOW_LOCAL_DIFFUSION_CANCEL
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_BACKGROUND_GENERATION
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DEMO_MODE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_COLOR_TOKEN
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_DARK_THEME
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_DARK_TOKEN
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_DYNAMIC_COLORS
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_SYSTEM_DARK_THEME
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DEVELOPER_MODE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_FORCE_SETUP_AFTER_UPDATE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_FORM_PROMPT_TAGGED_INPUT
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_GALLERY_GRID
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_HORDE_API_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_HUGGING_FACE_API_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_HUGGING_FACE_MODEL_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_LOCAL_DIFFUSION_SCHEDULER_THREAD
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_LOCAL_MODEL_ID
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_LOCAL_NN_API
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_MONITOR_CONNECTIVITY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_ON_BOARDING_COMPLETE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_OPEN_AI_API_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SAVE_TO_MEDIA_STORE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SD_MODEL
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SERVER_SOURCE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SERVER_URL
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_STABILITY_AI_API_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_STABILITY_AI_ENGINE_ID_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SWARM_MODEL
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SWARM_SERVER_URL
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.Backup
import com.shifthackz.aisdv1.domain.entity.BackupEntryToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.BackupRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.Date

internal class BackupRepositoryImpl(
    private val gson: Gson,
    private val generationLds: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val buildInfoProvider: BuildInfoProvider,
) : BackupRepository {

    override fun create(tokens: List<Pair<BackupEntryToken, Boolean>>): Single<ByteArray> {
        val chainGallery = if (tokens.contains(BackupEntryToken.Gallery to true)) {
            generationLds.queryAll()
        } else {
            Single.just(emptyList())
        }
        val chainAppConfig = if (tokens.contains(BackupEntryToken.AppConfiguration to true)) {
            val map = with(preferenceManager) {
                mapOf(
                    KEY_SERVER_URL to automatic1111ServerUrl,
                    KEY_SWARM_SERVER_URL to swarmUiServerUrl,
                    KEY_SWARM_MODEL to swarmUiModel,
                    KEY_DEMO_MODE to demoMode,
                    KEY_DEVELOPER_MODE to developerMode,
                    KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH to localDiffusionCustomModelPath,
                    KEY_ALLOW_LOCAL_DIFFUSION_CANCEL to localDiffusionAllowCancel,
                    KEY_LOCAL_DIFFUSION_SCHEDULER_THREAD to localDiffusionSchedulerThread,
                    KEY_MONITOR_CONNECTIVITY to monitorConnectivity,
                    KEY_AI_AUTO_SAVE to autoSaveAiResults,
                    KEY_SAVE_TO_MEDIA_STORE to saveToMediaStore,
                    KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS to formAdvancedOptionsAlwaysShow,
                    KEY_FORM_PROMPT_TAGGED_INPUT to formPromptTaggedInput,
                    KEY_SERVER_SOURCE to source,
                    KEY_SD_MODEL to sdModel,
                    KEY_HORDE_API_KEY to hordeApiKey,
                    KEY_OPEN_AI_API_KEY to openAiApiKey,
                    KEY_HUGGING_FACE_API_KEY to huggingFaceApiKey,
                    KEY_HUGGING_FACE_MODEL_KEY to huggingFaceModel,
                    KEY_STABILITY_AI_API_KEY to stabilityAiApiKey,
                    KEY_STABILITY_AI_ENGINE_ID_KEY to stabilityAiEngineId,
                    KEY_ON_BOARDING_COMPLETE to onBoardingComplete,
                    KEY_FORCE_SETUP_AFTER_UPDATE to forceSetupAfterUpdate,
                    KEY_LOCAL_MODEL_ID to localModelId,
                    KEY_LOCAL_NN_API to localUseNNAPI,
                    KEY_DESIGN_DYNAMIC_COLORS to designUseSystemColorPalette,
                    KEY_DESIGN_SYSTEM_DARK_THEME to designUseSystemDarkTheme,
                    KEY_DESIGN_DARK_THEME to designDarkTheme,
                    KEY_DESIGN_COLOR_TOKEN to designColorToken,
                    KEY_DESIGN_DARK_TOKEN to designDarkThemeToken,
                    KEY_BACKGROUND_GENERATION to backgroundGeneration,
                    KEY_GALLERY_GRID to galleryGrid,
                )
            }
            Single.just(map)
        } else {
            Single.just(emptyMap<String, Any>())
        }
        return Single
            .zip(chainGallery, chainAppConfig, ::Pair)
            .map { (gallery, config) ->
                Backup(
                    generatedAt = Date(),
                    appVersion = buildInfoProvider.toString(),
                    appConfiguration = config,
                    gallery = gallery,
                )
            }
            .map { backup -> gson.toJson(backup).encodeToByteArray() }
    }

    override fun restore(bytes: ByteArray): Completable = Single
        .just(bytes)
        .map { it.decodeToString() }
        .map { gson.fromJson<Backup>(it, object : TypeToken<Backup>() {}.type) }
        .flatMapCompletable { backup ->
            Completable.mergeArray(
                Completable.fromAction {
                    backup.appConfiguration.entries.forEach { (key, value) ->
                        when (key) {
                            KEY_SERVER_URL -> {
                                preferenceManager.automatic1111ServerUrl = value as String
                            }
                            KEY_SWARM_SERVER_URL -> {
                                preferenceManager.swarmUiServerUrl = value as String
                            }
                            KEY_SWARM_MODEL -> {
                                preferenceManager.swarmUiModel = value as String
                            }
                            KEY_DEMO_MODE -> {
                                preferenceManager.demoMode = value as Boolean
                            }
                            KEY_DEVELOPER_MODE -> {
                                preferenceManager.developerMode = value as Boolean
                            }
                            KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH -> {
                                preferenceManager.localDiffusionCustomModelPath = value as String
                            }
                            KEY_ALLOW_LOCAL_DIFFUSION_CANCEL -> {
                                preferenceManager.localDiffusionAllowCancel = value as Boolean
                            }
                            KEY_LOCAL_DIFFUSION_SCHEDULER_THREAD -> {
                                preferenceManager.localDiffusionSchedulerThread = value as SchedulersToken
                            }
                            KEY_MONITOR_CONNECTIVITY -> {
                                preferenceManager.monitorConnectivity = value as Boolean
                            }
                            KEY_AI_AUTO_SAVE -> {
                                preferenceManager.autoSaveAiResults = value as Boolean
                            }
                            KEY_SAVE_TO_MEDIA_STORE -> {
                                preferenceManager.saveToMediaStore = value as Boolean
                            }
                            KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS -> {
                                preferenceManager.formAdvancedOptionsAlwaysShow = value as Boolean
                            }
                            KEY_FORM_PROMPT_TAGGED_INPUT -> {
                                preferenceManager.formPromptTaggedInput = value as Boolean
                            }
                            KEY_SERVER_SOURCE -> {
                                preferenceManager.source = value as ServerSource
                            }
                            KEY_SD_MODEL -> {
                                preferenceManager.sdModel = value as String
                            }
                            KEY_HORDE_API_KEY -> {
                                preferenceManager.hordeApiKey = value as String
                            }
                            KEY_OPEN_AI_API_KEY -> {
                                preferenceManager.openAiApiKey = value as String
                            }
                            KEY_HUGGING_FACE_API_KEY -> {
                                preferenceManager.huggingFaceApiKey = value as String
                            }
                            KEY_HUGGING_FACE_MODEL_KEY -> {
                                preferenceManager.huggingFaceModel = value as String
                            }
                            KEY_STABILITY_AI_API_KEY -> {
                                preferenceManager.stabilityAiApiKey = value as String
                            }
                            KEY_STABILITY_AI_ENGINE_ID_KEY -> {
                                preferenceManager.stabilityAiEngineId = value as String
                            }
                            KEY_ON_BOARDING_COMPLETE -> {
                                preferenceManager.onBoardingComplete = value as Boolean
                            }
                            KEY_FORCE_SETUP_AFTER_UPDATE -> {
                                preferenceManager.forceSetupAfterUpdate = value as Boolean
                            }
                            KEY_LOCAL_MODEL_ID -> {
                                preferenceManager.localModelId = value as String
                            }
                            KEY_LOCAL_NN_API -> {
                                preferenceManager.localUseNNAPI = value as Boolean
                            }
                            KEY_DESIGN_DYNAMIC_COLORS -> {
                                preferenceManager.designUseSystemColorPalette = value as Boolean
                            }
                            KEY_DESIGN_SYSTEM_DARK_THEME -> {
                                preferenceManager.designUseSystemDarkTheme = value as Boolean
                            }
                            KEY_DESIGN_DARK_THEME -> {
                                preferenceManager.designDarkTheme = value as Boolean
                            }
                            KEY_DESIGN_COLOR_TOKEN -> {
                                preferenceManager.designColorToken = value as String
                            }
                            KEY_DESIGN_DARK_TOKEN -> {
                                preferenceManager.designDarkThemeToken = value as String
                            }
                            KEY_BACKGROUND_GENERATION -> {
                                preferenceManager.backgroundGeneration = value as Boolean
                            }
                            KEY_GALLERY_GRID -> {
                                preferenceManager.galleryGrid = value as Grid
                            }
                        }
                    }
                },
                generationLds.insert(backup.gallery),
            )
        }
}
