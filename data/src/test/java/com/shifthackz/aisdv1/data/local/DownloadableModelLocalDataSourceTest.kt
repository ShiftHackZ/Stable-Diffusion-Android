package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.data.mocks.mockLocalModelEntities
import com.shifthackz.aisdv1.data.mocks.mockLocalModelEntity
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.storage.db.persistent.dao.LocalModelDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class DownloadableModelLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
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
            stubDao.query()
        } returns Single.just(mockLocalModelEntities)

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        every {
            stubPreferenceManager.localModelId
        } returns ""

        val expected = mockLocalModelEntities.mapEntityToDomain()

        localDataSource
            .getAll()
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
            stubDao.query()
        } returns Single.just(emptyList())

        every {
            stubBuildInfoProvider.type
        } returns BuildType.PLAY

        every {
            stubPreferenceManager.localModelId
        } returns ""

        localDataSource
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all models, dao returns models list, app build type is FOSS, expected valid domain models list with CUSTOM model included`() {
        every {
            stubDao.query()
        } returns Single.just(mockLocalModelEntities)

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FOSS

        every {
            stubPreferenceManager.localModelId
        } returns ""

        val expected = buildList {
            addAll(mockLocalModelEntities.mapEntityToDomain())
            add(LocalAiModel.CUSTOM.copy(downloaded = true))
        }

        localDataSource
            .getAll()
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
            stubDao.query()
        } returns Single.just(emptyList())

        every {
            stubBuildInfoProvider.type
        } returns BuildType.FOSS

        every {
            stubPreferenceManager.localModelId
        } returns ""

        localDataSource
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(listOf(LocalAiModel.CUSTOM.copy(downloaded = true)))
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all models, dao throws exception, expected error value`() {
        every {
            stubDao.query()
        } returns Single.error(stubException)

        localDataSource
            .getAll()
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
            stubPreferenceManager.localModelId
        } returns ""

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
            stubPreferenceManager.localModelId
        } returns "5598"

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
            stubPreferenceManager.localModelId
        } returns "5598"

        val expected = mockLocalModelEntity.mapEntityToDomain().copy(selected = true)

        localDataSource
            .getSelected()
            .test()
            .assertNoErrors()
            .assertValue(expected)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get selected model, preference throws exception, expected error value`() {
        every {
            stubPreferenceManager.localModelId
        } returns ""

        localDataSource
            .getSelected()
            .test()
            .assertError { t ->
                t is IllegalStateException && t.message == "No selected model."
            }
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
