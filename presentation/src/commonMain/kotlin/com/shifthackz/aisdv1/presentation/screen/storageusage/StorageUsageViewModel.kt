package com.shifthackz.aisdv1.presentation.screen.storageusage

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalBonsaiModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalCoreMlModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalMediaPipeModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalSdxlModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.StorageUsageRouter
import com.shifthackz.aisdv1.presentation.screen.setup.viewmodel.setupAllowedModes
import com.shifthackz.aisdv1.presentation.screen.storageusage.model.StorageUsageIntent
import com.shifthackz.aisdv1.presentation.screen.storageusage.model.StorageUsageModal
import com.shifthackz.aisdv1.presentation.screen.storageusage.model.StorageUsageState
import com.shifthackz.aisdv1.presentation.screen.storageusage.platform.StorageUsagePlatformActions
import com.shifthackz.aisdv1.presentation.model.UsageCategory
import com.shifthackz.aisdv1.presentation.model.UsageItem
import com.shifthackz.aisdv1.presentation.model.UsageState
import com.shifthackz.aisdv1.presentation.model.isModelCategory
import com.shifthackz.aisdv1.presentation.model.isStorageCategory
import com.shifthackz.aisdv1.presentation.model.resolveSelectedCategory
import com.shifthackz.aisdv1.presentation.model.shouldUseCoreMlModelStoreFallback
import com.shifthackz.aisdv1.presentation.model.storageTextByteSize
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * ViewModel for the standalone storage usage screen.
 *
 * Storage usage is assembled from platform cache bytes, generated gallery payloads, and downloaded
 * model directories. The ViewModel listens to [StorageUsageObserver] instead of lifecycle callbacks
 * so Settings summaries and the standalone screen refresh from the same invalidation signal.
 *
 * @param dispatchersProvider App coroutine dispatchers used by the MVI base class and IO work.
 * @param getAllGalleryUseCase Reads generated gallery records whose payload strings are counted.
 * @param deleteAllGalleryUseCase Deletes gallery records after explicit user confirmation.
 * @param getLocalOnnxModelsUseCase Reads locally configured ONNX models.
 * @param getLocalMediaPipeModelsUseCase Reads locally configured MediaPipe models.
 * @param getLocalSdxlModelsUseCase Reads locally configured SDXL models.
 * @param getLocalCoreMlModelsUseCase Reads locally configured Core ML models on supported builds.
 * @param deleteModelUseCase Deletes downloaded local model metadata and files.
 * @param storageUsageObserver Shared invalidation stream used by Settings and this screen.
 * @param buildInfoProvider Build metadata used to filter platform-supported model categories.
 * @param router Standalone route navigation contract.
 * @param platformActions Filesystem bridge for cache and downloaded model byte accounting.
 * @param onError Error callback forwarded to the app-level error handling pipeline.
 *
 * @author Dmitriy Moroz
 */
class StorageUsageViewModel(
    dispatchersProvider: DispatchersProvider,
    private val getAllGalleryUseCase: GetAllGalleryUseCase,
    private val deleteAllGalleryUseCase: DeleteAllGalleryUseCase,
    private val getLocalOnnxModelsUseCase: GetLocalOnnxModelsUseCase,
    private val getLocalMediaPipeModelsUseCase: GetLocalMediaPipeModelsUseCase,
    private val getLocalSdxlModelsUseCase: GetLocalSdxlModelsUseCase,
    private val getLocalCoreMlModelsUseCase: GetLocalCoreMlModelsUseCase,
    private val getLocalBonsaiModelsUseCase: GetLocalBonsaiModelsUseCase,
    private val deleteModelUseCase: DeleteModelUseCase,
    private val storageUsageObserver: StorageUsageObserver,
    private val buildInfoProvider: BuildInfoProvider,
    private val router: StorageUsageRouter,
    private val platformActions: StorageUsagePlatformActions,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<StorageUsageState, StorageUsageIntent, EmptyEffect>(
    initialState = StorageUsageState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    private val ioDispatcher = dispatchersProvider.io

    init {
        observeStorageUsage()
    }

    override fun processIntent(intent: StorageUsageIntent) {
        when (intent) {
            StorageUsageIntent.NavigateBack -> router.navigateBack()

            is StorageUsageIntent.SelectCategory -> selectCategory(intent.category)

            is StorageUsageIntent.RequestClearCategory -> requestClearCategory(intent.category)

            is StorageUsageIntent.ConfirmClearCategory -> clearCategory(intent.category)

            StorageUsageIntent.RequestClearAll -> requestClearAll()

            is StorageUsageIntent.ConfirmClearAll -> clearAll(intent.categories.toSet())

            StorageUsageIntent.DismissDialog -> updateState {
                it.copy(screenModal = StorageUsageModal.None)
            }
        }
    }

    private fun selectCategory(category: UsageCategory) {
        if (!category.isStorageCategory()) return
        updateState { state ->
            state.copy(usage = state.usage.copy(selectedCategory = category))
        }
    }

    private fun observeStorageUsage() {
        updateState { state ->
            state.copy(usage = state.usage.copy(loading = true))
        }
        launch(ioDispatcher) {
            storageUsageObserver.observe()
                .map {
                    runCatching {
                        loadStorageUsage(currentState.usage.selectedCategory)
                    }
                        .onFailure(onError)
                        .getOrElse { currentState.usage.copy(loading = false) }
                }
                .distinctUntilChanged()
                .collect { usage ->
                    updateState { state -> state.copy(usage = usage) }
                }
        }
    }

    private suspend fun loadStorageUsage(
        selectedCategory: UsageCategory?,
    ): UsageState {
        val onnxModels = getDownloadedModels(getLocalOnnxModelsUseCase::invoke)
        val mediaPipeModels = getDownloadedModels(getLocalMediaPipeModelsUseCase::invoke)
        val sdxlModels = getDownloadedModels(getLocalSdxlModelsUseCase::invoke)
        val coreMlModels = getDownloadedModels(getLocalCoreMlModelsUseCase::invoke)
        val bonsaiModels = getDownloadedModels(getLocalBonsaiModelsUseCase::invoke)
        val coreMlModelIds = coreMlModels.map(LocalAiModel::id)
        val bonsaiModelIds = bonsaiModels.map(LocalAiModel::id)
        val allowedModes = buildInfoProvider.setupAllowedModes()
        val items = buildList {
            add(
                UsageItem(
                    category = UsageCategory.CACHE,
                    bytes = platformActions.mapStorageBytesForUi(platformActions.getAppCacheBytes()),
                ),
            )
            add(
                UsageItem(
                    category = UsageCategory.GALLERY,
                    bytes = getAllGalleryUseCase().sumOf { item ->
                        item.image.storageTextByteSize() + item.inputImage.storageTextByteSize()
                    },
                ),
            )
            if (ServerSource.LOCAL_MICROSOFT_ONNX in allowedModes) {
                add(
                    UsageItem(
                        category = UsageCategory.MODELS_ONNX,
                        bytes = platformActions.mapStorageBytesForUi(
                            platformActions.getDownloadedModelsBytes(onnxModels.map(LocalAiModel::id)),
                        ),
                        modelIds = onnxModels.map(LocalAiModel::id),
                    ),
                )
            }
            if (ServerSource.LOCAL_GOOGLE_MEDIA_PIPE in allowedModes) {
                add(
                    UsageItem(
                        category = UsageCategory.MODELS_MEDIAPIPE,
                        bytes = platformActions.mapStorageBytesForUi(
                            platformActions.getDownloadedModelsBytes(mediaPipeModels.map(LocalAiModel::id)),
                        ),
                        modelIds = mediaPipeModels.map(LocalAiModel::id),
                    ),
                )
            }
            if (ServerSource.LOCAL_STABLE_DIFFUSION_CPP in allowedModes) {
                add(
                    UsageItem(
                        category = UsageCategory.MODELS_SDXL,
                        bytes = platformActions.mapStorageBytesForUi(
                            platformActions.getDownloadedModelsBytes(sdxlModels.map(LocalAiModel::id)),
                        ),
                        modelIds = sdxlModels.map(LocalAiModel::id),
                    ),
                )
            }
            if (ServerSource.LOCAL_APPLE_CORE_ML in allowedModes) {
                add(
                    UsageItem(
                        category = UsageCategory.MODELS_CORE_ML,
                        bytes = platformActions.mapStorageBytesForUi(
                            getCoreMlModelBytes(
                                allowedModes = allowedModes,
                                modelIds = coreMlModelIds,
                            ),
                        ),
                        modelIds = coreMlModelIds,
                    ),
                )
            }
            if (ServerSource.LOCAL_APPLE_BONSAI in allowedModes) {
                add(
                    UsageItem(
                        category = UsageCategory.MODELS_BONSAI,
                        bytes = platformActions.mapStorageBytesForUi(
                            platformActions.getDownloadedModelsBytes(bonsaiModelIds),
                        ),
                        modelIds = bonsaiModelIds,
                    ),
                )
            }
        }
        return UsageState(
            loading = false,
            items = items,
            selectedCategory = items.resolveSelectedCategory(selectedCategory),
        )
    }

    private fun requestClearCategory(category: UsageCategory) {
        if (!category.isStorageCategory()) return
        val item = currentState.usage.items
            .firstOrNull { it.category == category && it.enabled }
            ?: return
        updateState { it.copy(screenModal = StorageUsageModal.ClearCategory(item)) }
    }

    private fun requestClearAll() {
        val items = currentState.usage.items
            .filter { item -> item.enabled && item.category.isStorageCategory() }
        if (items.isEmpty()) return
        updateState {
            it.copy(
                screenModal = StorageUsageModal.ClearAll(
                    items = items,
                    totalBytes = items.sumOf(UsageItem::bytes),
                ),
            )
        }
    }

    private fun clearCategory(category: UsageCategory) {
        if (!category.isStorageCategory()) return
        updateState {
            it.copy(
                screenModal = StorageUsageModal.None,
                usage = it.usage.copy(loading = true),
            )
        }
        launch(ioDispatcher) {
            runCatching {
                clearCategoryInternal(category)
            }
                .onSuccess { storageUsageObserver.notifyChanged() }
                .onFailure { t ->
                    onError(t)
                    updateState {
                        it.copy(
                            screenModal = StorageUsageModal.None,
                            usage = it.usage.copy(loading = false),
                        )
                    }
                }
        }
    }

    private fun clearAll(categories: Set<UsageCategory>) {
        if (categories.isEmpty()) {
            updateState { it.copy(screenModal = StorageUsageModal.None) }
            return
        }
        updateState {
            it.copy(
                screenModal = StorageUsageModal.None,
                usage = it.usage.copy(loading = true),
            )
        }
        launch(ioDispatcher) {
            runCatching {
                if (UsageCategory.CACHE in categories) {
                    platformActions.clearAppCache()
                }
                if (UsageCategory.GALLERY in categories) {
                    deleteAllGalleryUseCase()
                }
                if (UsageCategory.MODELS_CORE_ML in categories && shouldUseCoreMlModelStoreFallback()) {
                    platformActions.clearAllDownloadedModels()
                }
                currentState.usage.items
                    .filter { item -> item.category in categories && item.category.isModelCategory() }
                    .flatMap(UsageItem::modelIds)
                    .distinct()
                    .forEach { id -> deleteModelUseCase(id) }
            }
                .onSuccess { storageUsageObserver.notifyChanged() }
                .onFailure { t ->
                    onError(t)
                    updateState {
                        it.copy(
                            screenModal = StorageUsageModal.None,
                            usage = it.usage.copy(loading = false),
                        )
                    }
                }
        }
    }

    private suspend fun clearCategoryInternal(category: UsageCategory) {
        when (category) {
            UsageCategory.CACHE -> platformActions.clearAppCache()
            UsageCategory.GALLERY -> deleteAllGalleryUseCase()
            UsageCategory.MODELS_ONNX,
            UsageCategory.MODELS_MEDIAPIPE,
            UsageCategory.MODELS_SDXL,
            UsageCategory.MODELS_CORE_ML,
            UsageCategory.MODELS_BONSAI,
            -> currentState.usage.items
                .firstOrNull { it.category == category }
                ?.modelIds
                ?.forEach { id -> deleteModelUseCase(id) }
                .also {
                    if (category == UsageCategory.MODELS_CORE_ML && shouldUseCoreMlModelStoreFallback()) {
                        platformActions.clearAllDownloadedModels()
                    }
                }

            UsageCategory.TRAFFIC_MODELS,
            UsageCategory.TRAFFIC_CONFIGS,
            UsageCategory.TRAFFIC_INFERENCE,
            -> Unit
        }
    }

    /**
     * Reads Core ML model bytes from the entire platform model store on iOS-style targets.
     *
     * @param allowedModes Providers available on the current platform.
     * @param modelIds Catalog model identifiers known to the app.
     * @return Filesystem byte count for the Core ML storage row.
     * @author Dmitriy Moroz
     */
    private suspend fun getCoreMlModelBytes(
        allowedModes: List<ServerSource>,
        modelIds: List<String>,
    ): Long =
        if (allowedModes.shouldUseCoreMlModelStoreFallback()) {
            platformActions.getAllDownloadedModelsBytes()
        } else {
            platformActions.getDownloadedModelsBytes(modelIds)
        }

    /**
     * Returns true when Core ML is the only local model provider exposed by this platform.
     *
     * @return True for iOS targets where all app-private model bytes belong to Core ML.
     * @author Dmitriy Moroz
     */
    private fun shouldUseCoreMlModelStoreFallback(): Boolean =
        buildInfoProvider.setupAllowedModes().shouldUseCoreMlModelStoreFallback()

    private suspend fun getDownloadedModels(
        getModels: suspend () -> List<LocalAiModel>,
    ): List<LocalAiModel> = runCatching { getModels() }
        .onFailure(onError)
        .getOrDefault(emptyList())
        .filter { model ->
            model.downloaded &&
                model.id !in customModelIds
        }
}

/**
 * Custom local model placeholders are user-managed paths and must not count as downloaded files.
 *
 * @author Dmitriy Moroz
 */
private val customModelIds = setOf(
    LocalAiModel.CustomOnnx.id,
    LocalAiModel.CustomMediaPipe.id,
    LocalAiModel.CustomSdxl.id,
    LocalAiModel.CustomCoreMl.id,
    LocalAiModel.CustomBonsai.id,
)
