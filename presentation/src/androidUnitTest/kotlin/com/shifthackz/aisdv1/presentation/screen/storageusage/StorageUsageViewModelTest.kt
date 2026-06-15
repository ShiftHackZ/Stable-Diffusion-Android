package com.shifthackz.aisdv1.presentation.screen.storageusage

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalBonsaiModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalCoreMlModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalMediaPipeModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalSdxlModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.StorageUsageRouter
import com.shifthackz.aisdv1.presentation.screen.storageusage.model.StorageUsageIntent
import com.shifthackz.aisdv1.presentation.screen.storageusage.model.StorageUsageModal
import com.shifthackz.aisdv1.presentation.screen.storageusage.platform.StorageUsagePlatformActions
import com.shifthackz.aisdv1.presentation.model.UsageCategory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Verifies storage usage loading, confirmation dialogs, and observer-driven refreshes.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StorageUsageViewModelTest {

    private val getAllGalleryUseCase = mockk<GetAllGalleryUseCase>()
    private val deleteAllGalleryUseCase = mockk<DeleteAllGalleryUseCase>()
    private val getLocalOnnxModelsUseCase = mockk<GetLocalOnnxModelsUseCase>()
    private val getLocalMediaPipeModelsUseCase = mockk<GetLocalMediaPipeModelsUseCase>()
    private val getLocalSdxlModelsUseCase = mockk<GetLocalSdxlModelsUseCase>()
    private val getLocalCoreMlModelsUseCase = mockk<GetLocalCoreMlModelsUseCase>()
    private val getLocalBonsaiModelsUseCase = mockk<GetLocalBonsaiModelsUseCase>()
    private val deleteModelUseCase = mockk<DeleteModelUseCase>()
    private val storageUsageObserver = StorageUsageObserver()
    private val router = TestStorageUsageRouter()
    private val platformActions = TestStorageUsagePlatformActions()
    private var onnxModels = emptyList<LocalAiModel>()
    private lateinit var dispatchersProvider: DispatchersProvider
    private var viewModelScope: CoroutineScope? = null

    @Before
    fun initialize() {
        coEvery { getAllGalleryUseCase() } returns emptyList()
        coEvery { deleteAllGalleryUseCase() } returns Unit
        coEvery { getLocalOnnxModelsUseCase() } answers { onnxModels }
        coEvery { getLocalMediaPipeModelsUseCase() } returns emptyList()
        coEvery { getLocalSdxlModelsUseCase() } returns emptyList()
        coEvery { getLocalCoreMlModelsUseCase() } returns emptyList()
        coEvery { getLocalBonsaiModelsUseCase() } returns emptyList()
        coEvery { deleteModelUseCase(any()) } returns Unit
    }

    @After
    fun tearDown() {
        viewModelScope?.cancel()
        viewModelScope = null
        onnxModels = emptyList()
        platformActions.reset()
        router.backInvocations = 0
    }

    @Test
    fun `initialized, expected real storage usage loaded`() = runTest {
        platformActions.appCacheBytes = 2048L
        val viewModel = createViewModel()
        advanceUntilIdle()

        val actual = viewModel.state.value.usage
        assertEquals(false, actual.loading)
        assertEquals(2048L, actual.totalBytes)
        assertEquals(2048L, actual.items.first { it.category == UsageCategory.CACHE }.bytes)
    }

    @Test
    fun `given clear all requested, expected confirmation before deleting cache`() = runTest {
        platformActions.appCacheBytes = 2048L
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(StorageUsageIntent.RequestClearAll)
        advanceUntilIdle()

        val modal = viewModel.state.value.screenModal
        assertTrue(modal is StorageUsageModal.ClearAll)
        assertEquals(2048L, platformActions.appCacheBytes)

        viewModel.processIntent(
            StorageUsageIntent.ConfirmClearAll(
                categories = (modal as StorageUsageModal.ClearAll).items.map { it.category },
            ),
        )
        assertEquals(StorageUsageModal.None, viewModel.state.value.screenModal)
        assertEquals(true, viewModel.state.value.usage.loading)
        advanceUntilIdle()

        assertEquals(0L, platformActions.appCacheBytes)
        assertEquals(0L, viewModel.state.value.usage.totalBytes)
    }

    @Test
    fun `given category clear requested, expected confirmation before deleting cache`() = runTest {
        platformActions.appCacheBytes = 1024L
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(StorageUsageIntent.RequestClearCategory(UsageCategory.CACHE))
        advanceUntilIdle()

        val modal = viewModel.state.value.screenModal
        assertTrue(modal is StorageUsageModal.ClearCategory)
        assertEquals(1024L, (modal as StorageUsageModal.ClearCategory).item.bytes)

        viewModel.processIntent(StorageUsageIntent.ConfirmClearCategory(UsageCategory.CACHE))
        assertEquals(StorageUsageModal.None, viewModel.state.value.screenModal)
        assertEquals(true, viewModel.state.value.usage.loading)
        advanceUntilIdle()

        assertEquals(0L, platformActions.appCacheBytes)
        assertEquals(0L, viewModel.state.value.usage.totalBytes)
    }

    @Test
    fun `given downloaded model appears, expected observer reloads storage usage`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(0L, viewModel.state.value.usage.totalBytes)

        onnxModels = listOf(downloadedModel("onnx-1", LocalAiModel.Type.ONNX))
        platformActions.modelBytes["onnx-1"] = 4096L
        storageUsageObserver.notifyChanged()
        advanceUntilIdle()

        val actual = viewModel.state.value.usage
        assertEquals(4096L, actual.totalBytes)
        assertEquals(4096L, actual.items.first { it.category == UsageCategory.MODELS_ONNX }.bytes)
    }

    @Test
    fun `given platform hides filesystem residue, expected usage UI remains empty`() = runTest {
        platformActions.visibleStorageThresholdBytes = 1024L
        platformActions.appCacheBytes = 64L
        onnxModels = listOf(downloadedModel("onnx-residue", LocalAiModel.Type.ONNX))
        platformActions.modelBytes["onnx-residue"] = 64L
        val viewModel = createViewModel()
        advanceUntilIdle()

        val actual = viewModel.state.value.usage

        assertEquals(0L, actual.totalBytes)
        assertEquals(0L, actual.items.first { it.category == UsageCategory.CACHE }.bytes)
        assertEquals(0L, actual.items.first { it.category == UsageCategory.MODELS_ONNX }.bytes)
        assertEquals(64L, platformActions.appCacheBytes)
        assertEquals(64L, platformActions.modelBytes.getValue("onnx-residue"))
    }

    @Test
    fun `given back intent, expected router navigates back`() = runTest {
        val viewModel = createViewModel()

        viewModel.processIntent(StorageUsageIntent.NavigateBack)

        assertEquals(1, router.backInvocations)
    }

    private fun TestScope.createViewModel(): StorageUsageViewModel {
        val dispatcher = StandardTestDispatcher(testScheduler)
        dispatchersProvider = object : DispatchersProvider {
            override val io: CoroutineDispatcher = dispatcher
            override val ui: CoroutineDispatcher = dispatcher
            override val immediate: CoroutineDispatcher = dispatcher
        }
        viewModelScope = CoroutineScope(SupervisorJob() + dispatcher)
        return StorageUsageViewModel(
            dispatchersProvider = dispatchersProvider,
            getAllGalleryUseCase = getAllGalleryUseCase,
            deleteAllGalleryUseCase = deleteAllGalleryUseCase,
            getLocalOnnxModelsUseCase = getLocalOnnxModelsUseCase,
            getLocalMediaPipeModelsUseCase = getLocalMediaPipeModelsUseCase,
            getLocalSdxlModelsUseCase = getLocalSdxlModelsUseCase,
            getLocalCoreMlModelsUseCase = getLocalCoreMlModelsUseCase,
            getLocalBonsaiModelsUseCase = getLocalBonsaiModelsUseCase,
            deleteModelUseCase = deleteModelUseCase,
            storageUsageObserver = storageUsageObserver,
            buildInfoProvider = TestBuildInfoProvider,
            router = router,
            platformActions = platformActions,
        )
    }
}

/**
 * Creates a downloaded local AI model fixture for storage usage aggregation tests.
 *
 * @param id Stable local model identifier used by the storage usage row.
 * @param type Local model provider type used to group storage categories.
 *
 * @author Dmitriy Moroz
 */
private fun downloadedModel(
    id: String,
    type: LocalAiModel.Type,
) = LocalAiModel(
    id = id,
    type = type,
    name = id,
    size = "1 MB",
    sources = emptyList(),
    downloaded = true,
)

/**
 * Full-build test provider so all local model categories are allowed during storage tests.
 *
 * @author Dmitriy Moroz
 */
private object TestBuildInfoProvider : BuildInfoProvider {
    override val isDebug: Boolean = true
    override val buildNumber: Int = 5598
    override val version: BuildVersion = BuildVersion()
    override val type: BuildType = BuildType.FULL
}

/**
 * Router spy used to assert standalone screen back navigation.
 *
 * @author Dmitriy Moroz
 */
private class TestStorageUsageRouter : StorageUsageRouter {
    var backInvocations = 0

    override fun navigateBack() {
        backInvocations++
    }
}

/**
 * In-memory platform bridge that lets tests mutate cache and model sizes deterministically.
 *
 * @author Dmitriy Moroz
 */
private class TestStorageUsagePlatformActions : StorageUsagePlatformActions {
    var appCacheBytes = 0L
    var visibleStorageThresholdBytes = 0L
    val modelBytes = mutableMapOf<String, Long>()

    override fun mapStorageBytesForUi(bytes: Long): Long {
        val safeBytes = bytes.coerceAtLeast(0L)
        return if (safeBytes < visibleStorageThresholdBytes) 0L else safeBytes
    }

    override suspend fun getAppCacheBytes(): Long = appCacheBytes

    override suspend fun clearAppCache() {
        appCacheBytes = 0L
    }

    override suspend fun getAllDownloadedModelsBytes(): Long = modelBytes.values.sum()

    override suspend fun clearAllDownloadedModels() {
        modelBytes.clear()
    }

    override suspend fun getDownloadedModelsBytes(modelIds: List<String>): Long =
        modelIds.distinct().sumOf { id -> modelBytes[id] ?: 0L }

    fun reset() {
        appCacheBytes = 0L
        visibleStorageThresholdBytes = 0L
        modelBytes.clear()
    }
}
