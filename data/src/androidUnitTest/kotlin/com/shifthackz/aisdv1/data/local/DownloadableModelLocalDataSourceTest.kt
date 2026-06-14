package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.data.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.data.mocks.mockLocalModelEntities
import com.shifthackz.aisdv1.data.mocks.mockLocalModelEntity
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.storage.db.persistent.dao.LocalModelDao
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Verifies downloadable local model queries, selected model refreshes, and build-type filtering.
 *
 * @author Dmitriy Moroz
 */
class DownloadableModelLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubFileStore = mockk<DownloadableModelFileStore>()
    private val stubDao = mockk<LocalModelDao>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubBuildInfoProvider = mockk<BuildInfoProvider>()

    private val localDataSource = DownloadableModelLocalDataSource(
        dao = stubDao,
        preferenceManager = stubPreferenceManager,
        buildInfoProvider = stubBuildInfoProvider,
        fileStore = stubFileStore,
    )

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        every {
            stubPreferenceManager.localMediaPipeModelId
        } returns ""

        every {
            stubPreferenceManager.localCoreMlModelId
        } returns ""

        every {
            stubPreferenceManager.localSdxlModelId
        } returns ""

        every {
            stubPreferenceManager.observe()
        } returns flowOf(Settings())

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        every { stubFileStore.isDownloaded(any()) } returns false
        every {
            stubFileStore.isDownloaded(
                match { model ->
                    model.id == LocalAiModel.CustomOnnx.id ||
                        model.id == LocalAiModel.CustomMediaPipe.id ||
                        model.id == LocalAiModel.CustomSdxl.id
                },
            )
        } returns true
    }

    @Test
    fun `given attempt to get all models, dao returns models list, app build type is PLAY, expected valid domain models list`() = runTest {
        coEvery {
            stubDao.queryByType(any())
        } returns mockLocalModelEntities

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        val expected = mockLocalModelEntities.mapEntityToDomain()

        val actual = localDataSource.getAllOnnx()

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given attempt to get all models, dao returns empty models list, app build type is PLAY, expected empty domain models list`() = runTest {
        coEvery {
            stubDao.queryByType(any())
        } returns emptyList()

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        val actual = localDataSource.getAllOnnx()

        Assert.assertEquals(emptyList<LocalAiModel>(), actual)
    }

    @Test
    fun `given attempt to get all models, dao returns models list, app build type is FOSS, expected valid domain models list with CUSTOM model included`() = runTest {
        coEvery {
            stubDao.queryByType(any())
        } returns mockLocalModelEntities

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FOSS

        val expected = buildList {
            addAll(mockLocalModelEntities.mapEntityToDomain())
            add(LocalAiModel.CustomOnnx.copy(downloaded = true))
        }

        val actual = localDataSource.getAllOnnx()

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given attempt to get all models, dao returns empty models list, app build type is FOSS, expected domain models list with only CUSTOM model included`() = runTest {
        coEvery {
            stubDao.queryByType(any())
        } returns emptyList()

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FOSS

        val actual = localDataSource.getAllOnnx()

        Assert.assertEquals(listOf(LocalAiModel.CustomOnnx.copy(downloaded = true)), actual)
    }

    @Test
    fun `given attempt to get all models, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryByType(any())
        } throws stubException

        val actual = runCatching { localDataSource.getAllOnnx() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get all sdxl models, dao returns models list, app build type is PLAY, expected custom model excluded`() = runTest {
        val sdxlEntities = listOf(mockLocalModelEntity.copy(type = LocalAiModel.Type.Sdxl.key))
        coEvery {
            stubDao.queryByType(LocalAiModel.Type.Sdxl.key)
        } returns sdxlEntities

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        val expected = sdxlEntities.mapEntityToDomain()

        val actual = localDataSource.getAllSdxl()

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given attempt to get all sdxl models, dao returns empty list, app build type is FULL, expected custom model included`() = runTest {
        coEvery {
            stubDao.queryByType(LocalAiModel.Type.Sdxl.key)
        } returns emptyList()

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FULL

        val actual = localDataSource.getAllSdxl()

        Assert.assertEquals(listOf(LocalAiModel.CustomSdxl.copy(downloaded = true)), actual)
    }

    @Test
    fun `given attempt to get selected sdxl model, preference points to custom sdxl, expected custom selected`() = runTest {
        every {
            stubPreferenceManager.localSdxlModelId
        } returns LocalAiModel.CustomSdxl.id

        val actual = localDataSource.getSelectedSdxl()

        Assert.assertEquals(LocalAiModel.CustomSdxl.copy(downloaded = true, selected = true), actual)
    }

    @Test
    fun `given attempt to get model by id, dao returns model, model id does not match local model id in preference, expected valid domain model value with selected equals false`() = runTest {
        coEvery {
            stubDao.queryById(any())
        } returns mockLocalModelEntity

        val expected = mockLocalModelEntity.mapEntityToDomain()

        val actual = localDataSource.getById("5598")

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given attempt to get model by id, dao returns model, model id matches local model id in preference, expected valid domain model value with selected equals true`() = runTest {
        coEvery {
            stubDao.queryById(any())
        } returns mockLocalModelEntity

        every {
            stubPreferenceManager.localOnnxModelId
        } returns "5598"

        val expected = mockLocalModelEntity.mapEntityToDomain().copy(selected = true)

        val actual = localDataSource.getById("5598")

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given attempt to get model by id, dao throws exception, expected error true`() = runTest {
        coEvery {
            stubDao.queryById(any())
        } throws stubException

        val actual = runCatching { localDataSource.getById("5598") }

        Assert.assertEquals(stubException.message, actual.exceptionOrNull()?.message)
    }

    @Test
    fun `given attempt to get selected model, dao has model with provided id in db, expected domain valid model value`() = runTest {
        coEvery {
            stubDao.queryById(any())
        } returns mockLocalModelEntity

        every {
            stubPreferenceManager.localOnnxModelId
        } returns "5598"

        val expected = mockLocalModelEntity.mapEntityToDomain().copy(selected = true)

        val actual = localDataSource.getSelectedOnnx()

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given attempt to get selected model, preference has no selected model, expected error value`() = runTest {
        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        val actual = runCatching { localDataSource.getSelectedOnnx() }

        Assert.assertTrue(actual.exceptionOrNull() is IllegalStateException)
        Assert.assertEquals("No selected model.", actual.exceptionOrNull()?.message)
    }

    @Test
    fun `given attempt to observe all models, dao emits empty list, then list with two items, app build type is PLAY, expected empty list, then domain list with two items`() = runTest {
        every {
            stubDao.observeByType(any())
        } returns flowOf(emptyList(), mockLocalModelEntities)

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        val actual = localDataSource.observeAllOnnx().toList()

        Assert.assertEquals(listOf(emptyList(), mockLocalModelEntities.mapEntityToDomain()), actual)
    }

    @Test
    fun `given attempt to observe all models, dao emits empty list, then list with two items, app build type is FOSS, expected list with only CUSTOM model included, then domain list with two items and CUSTOM`() = runTest {
        every {
            stubDao.observeByType(any())
        } returns flowOf(emptyList(), mockLocalModelEntities)

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FOSS

        val actual = localDataSource.observeAllOnnx().toList()

        Assert.assertEquals(
            listOf(
                listOf(LocalAiModel.CustomOnnx.copy(downloaded = true)),
                buildList {
                    addAll(mockLocalModelEntities.mapEntityToDomain())
                    add(LocalAiModel.CustomOnnx.copy(downloaded = true))
                }
            ),
            actual,
        )
    }

    @Test
    fun `given attempt to observe all models, dao throws exception, expected error value`() = runTest {
        every {
            stubDao.observeByType(any())
        } returns flow { throw stubException }
        every {
            stubPreferenceManager.observe()
        } returns flow {
            emit(Settings())
            awaitCancellation()
        }

        val actual = runCatching { localDataSource.observeAllOnnx().toList() }

        Assert.assertEquals(stubException.message, actual.exceptionOrNull()?.message)
    }

    @Test
    fun `given attempt to observe all onnx models, preference changes, expected local state refreshed`() = runTest {
        val onnxModel = mockLocalModelEntity.mapEntityToDomain()
        var selectedModelId = ""

        every {
            stubDao.observeByType(LocalAiModel.Type.ONNX.key)
        } returns flow {
            emit(listOf(mockLocalModelEntity))
            awaitCancellation()
        }

        every {
            stubPreferenceManager.observe()
        } returns flow {
            emit(Settings())
            delay(1L)
            selectedModelId = mockLocalModelEntity.id
            emit(Settings(languageCode = "refresh"))
        }

        every {
            stubPreferenceManager.localOnnxModelId
        } answers { selectedModelId }

        every {
            stubFileStore.isDownloaded(match { model -> model.id == mockLocalModelEntity.id })
        } answers { selectedModelId == mockLocalModelEntity.id }

        val actual = localDataSource.observeAllOnnx().take(2).toList()

        Assert.assertEquals(
            listOf(
                listOf(onnxModel),
                listOf(onnxModel.copy(downloaded = true, selected = true)),
            ),
            actual,
        )
    }

    @Test
    fun `given attempt to observe all core ml models, preference changes, expected local state refreshed`() = runTest {
        val coreMlEntity = mockLocalModelEntity.copy(type = LocalAiModel.Type.CoreMl.key)
        val coreMlModel = coreMlEntity.mapEntityToDomain()
        var selectedModelId = ""

        every {
            stubDao.observeByType(LocalAiModel.Type.CoreMl.key)
        } returns flow {
            emit(listOf(coreMlEntity))
            awaitCancellation()
        }

        every {
            stubPreferenceManager.observe()
        } returns flow {
            emit(Settings())
            delay(1L)
            selectedModelId = coreMlEntity.id
            emit(Settings(languageCode = "refresh"))
        }

        every {
            stubPreferenceManager.localCoreMlModelId
        } answers { selectedModelId }

        every {
            stubFileStore.isDownloaded(match { model -> model.id == coreMlEntity.id })
        } answers { selectedModelId == coreMlEntity.id }

        val actual = localDataSource.observeAllCoreMl().take(2).toList()

        Assert.assertEquals(
            listOf(
                listOf(coreMlModel),
                listOf(coreMlModel.copy(downloaded = true, selected = true)),
            ),
            actual,
        )
    }

    @Test
    fun `given attempt to save local model list, dao insert success, expected complete value`() = runTest {
        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.save(mockLocalAiModels) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to save local model list, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.insertList(any())
        } throws stubException

        val actual = runCatching { localDataSource.save(mockLocalAiModels) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to delete file, delete operation success, expected complete value`() = runTest {
        every {
            stubFileStore.delete(any())
        } returns Unit

        val actual = runCatching { localDataSource.delete("5598") }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to delete file, delete operation failed, expected error value`() = runTest {
        every {
            stubFileStore.delete(any())
        } throws stubException

        val actual = runCatching { localDataSource.delete("5598") }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
