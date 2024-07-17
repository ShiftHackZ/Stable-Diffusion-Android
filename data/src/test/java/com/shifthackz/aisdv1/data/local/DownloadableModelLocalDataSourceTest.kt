package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.data.mocks.mockLocalModelEntities
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
}
