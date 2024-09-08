package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.data.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.data.mocks.mockLocalModelEntities
import com.shifthackz.aisdv1.data.mocks.mockLocalModelEntity
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.storage.db.persistent.dao.LocalModelDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Assert
import org.junit.Test

class DownloadableModelLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubLocalModels = BehaviorSubject.create<List<LocalModelEntity>>()
    private val stubFileProviderDescriptor = mockk<FileProviderDescriptor>()
    private val stubDao = mockk<LocalModelDao>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubBuildInfoProvider = mockk<BuildInfoProvider>()

    private val localDataSource = DownloadableModelLocalDataSource(
        fileProviderDescriptor = stubFileProviderDescriptor,
        dao = stubDao,
        preferenceManager = stubPreferenceManager,
        buildInfoProvider = stubBuildInfoProvider,
    )

    @Test
    fun `given attempt to get all models, dao returns models list, app build type is PLAY, expected valid domain models list`() {
        every {
            stubDao.queryByType(any())
        } returns Single.just(mockLocalModelEntities)

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        every {
            stubFileProviderDescriptor.localModelDirPath
        } returns "/tmp/local"

        val expected = mockLocalModelEntities.mapEntityToDomain()

        localDataSource
            .getAllOnnx()
            .test()
            .assertNoErrors()
            .assertValue { actual ->
                expected == actual && expected.size == actual.size
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all models, dao returns empty models list, app build type is PLAY, expected empty domain models list`() {
        every {
            stubDao.queryByType(any())
        } returns Single.just(emptyList())

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        localDataSource
            .getAllOnnx()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all models, dao returns models list, app build type is FOSS, expected valid domain models list with CUSTOM model included`() {
        every {
            stubDao.queryByType(any())
        } returns Single.just(mockLocalModelEntities)

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FOSS

        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        every {
            stubFileProviderDescriptor.localModelDirPath
        } returns "/tmp/local"

        val expected = buildList {
            addAll(mockLocalModelEntities.mapEntityToDomain())
            add(LocalAiModel.CustomOnnx.copy(downloaded = true))
        }

        localDataSource
            .getAllOnnx()
            .test()
            .assertNoErrors()
            .assertValue { actual ->
                expected == actual && expected.size == actual.size
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all models, dao returns empty models list, app build type is FOSS, expected domain models list with only CUSTOM model included`() {
        every {
            stubDao.queryByType(any())
        } returns Single.just(emptyList())

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FOSS

        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        localDataSource
            .getAllOnnx()
            .test()
            .assertNoErrors()
            .assertValue(listOf(LocalAiModel.CustomOnnx.copy(downloaded = true)))
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all models, dao throws exception, expected error value`() {
        every {
            stubDao.queryByType(any())
        } returns Single.error(stubException)

        localDataSource
            .getAllOnnx()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get model by id, dao returns model, model id does not match local model id in preference, expected valid domain model value with selected equals false`() {
        every {
            stubDao.queryById(any())
        } returns Single.just(mockLocalModelEntity)

        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        every {
            stubFileProviderDescriptor.localModelDirPath
        } returns "/tmp/local"

        val expected = mockLocalModelEntity.mapEntityToDomain()

        localDataSource
            .getById("5598")
            .test()
            .assertNoErrors()
            .assertValue(expected)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get model by id, dao returns model, model id matches local model id in preference, expected valid domain model value with selected equals true`() {
        every {
            stubDao.queryById(any())
        } returns Single.just(mockLocalModelEntity)

        every {
            stubPreferenceManager.localOnnxModelId
        } returns "5598"

        every {
            stubFileProviderDescriptor.localModelDirPath
        } returns "/tmp/local"

        val expected = mockLocalModelEntity.mapEntityToDomain().copy(selected = true)

        localDataSource
            .getById("5598")
            .test()
            .assertNoErrors()
            .assertValue(expected)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get model by id, dao throws exception, expected error true`() {
        every {
            stubDao.queryById(any())
        } returns Single.error(stubException)

        localDataSource
            .getById("5598")
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get selected model, dao has model with provided id in db, expected domain valid model value`() {
        every {
            stubDao.queryById(any())
        } returns Single.just(mockLocalModelEntity)

        every {
            stubPreferenceManager.localOnnxModelId
        } returns "5598"

        every {
            stubFileProviderDescriptor.localModelDirPath
        } returns "/tmp/local"

        val expected = mockLocalModelEntity.mapEntityToDomain().copy(selected = true)

        localDataSource
            .getSelectedOnnx()
            .test()
            .assertNoErrors()
            .assertValue(expected)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get selected model, preference throws exception, expected error value`() {
        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        localDataSource
            .getSelectedOnnx()
            .test()
            .assertError { t ->
                t is IllegalStateException && t.message == "No selected model."
            }
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to observe all models, dao emits empty list, then list with two items, app build type is PLAY, expected empty list, then domain list with two items`() {
        every {
            stubDao.observeByType(any())
        } returns stubLocalModels.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        every {
            stubFileProviderDescriptor.localModelDirPath
        } returns "/tmp/local"

        val stubObserver = localDataSource
            .observeAllOnnx()
            .test()

        stubLocalModels.onNext(emptyList())

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, emptyList())

        stubLocalModels.onNext(mockLocalModelEntities)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, mockLocalModelEntities.mapEntityToDomain())
    }

    @Test
    fun `given attempt to observe all models, dao emits empty list, then list with two items, app build type is FOSS, expected list with only CUSTOM model included, then domain list with two items and CUSTOM`() {
        every {
            stubDao.observeByType(any())
        } returns stubLocalModels.toFlowable(BackpressureStrategy.LATEST)

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FOSS

        every {
            stubPreferenceManager.localOnnxModelId
        } returns ""

        every {
            stubFileProviderDescriptor.localModelDirPath
        } returns "/tmp/local"

        val stubObserver = localDataSource
            .observeAllOnnx()
            .test()

        stubLocalModels.onNext(emptyList())

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, listOf(LocalAiModel.CustomOnnx.copy(downloaded = true)))

        stubLocalModels.onNext(mockLocalModelEntities)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, buildList {
                addAll(mockLocalModelEntities.mapEntityToDomain())
                add(LocalAiModel.CustomOnnx.copy(downloaded = true))
            })
    }

    @Test
    fun `given attempt to observe all models, dao throws exception, expected error value`() {
        every {
            stubDao.observeByType(any())
        } returns Flowable.error(stubException)

        localDataSource
            .observeAllOnnx()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to save local model list, dao insert success, expected complete value`() {
        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .save(mockLocalAiModels)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to save local model list, dao throws exception, expected error value`() {
        every {
            stubDao.insertList(any())
        } returns Completable.error(stubException)

        localDataSource
            .save(mockLocalAiModels)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to delete file, delete operation success, expected complete value`() {
        every {
            stubFileProviderDescriptor.localModelDirPath
        } returns "/tmp/local"

        localDataSource
            .delete("5598")
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to delete file, delete operation failed, expected error value`() {
        every {
            stubFileProviderDescriptor.localModelDirPath
        } throws stubException

        localDataSource
            .delete("5598")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
