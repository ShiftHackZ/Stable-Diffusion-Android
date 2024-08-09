package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResults
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GenerationResultRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubMediaStoreGateway = mockk<MediaStoreGateway>()
    private val stubBase64ToBitmapConverter = mockk<Base64ToBitmapConverter>()
    private val stubLocalDataSource = mockk<GenerationResultDataSource.Local>()

    private val repository = GenerationResultRepositoryImpl(
        preferenceManager = stubPreferenceManager,
        mediaStoreGateway = stubMediaStoreGateway,
        base64ToBitmapConverter = stubBase64ToBitmapConverter,
        localDataSource = stubLocalDataSource,
    )

    @Test
    fun `given attempt to get all, local returns data, expected valid domain model list value`() {
        every {
            stubLocalDataSource.queryAll()
        } returns Single.just(mockAiGenerationResults)

        repository
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResults)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all, local returns empty data, expected empty domain model list value`() {
        every {
            stubLocalDataSource.queryAll()
        } returns Single.just(emptyList())

        repository
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all, local throws exception, expected error value`() {
        every {
            stubLocalDataSource.queryAll()
        } returns Single.error(stubException)

        repository
            .getAll()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get page, local returns data, expected valid domain model list value`() {
        every {
            stubLocalDataSource.queryPage(any(), any())
        } returns Single.just(mockAiGenerationResults)

        repository
            .getPage(20, 0)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResults)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get page, local returns empty data, expected empty domain model list value`() {
        every {
            stubLocalDataSource.queryPage(any(), any())
        } returns Single.just(emptyList())

        repository
            .getPage(20, 0)
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get page, local throws exception, expected error value`() {
        every {
            stubLocalDataSource.queryPage(any(), any())
        } returns Single.error(stubException)

        repository
            .getPage(20, 0)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get media store info, gateway returned data, expected valid media store info value`() {
        every {
            stubMediaStoreGateway.getInfo()
        } returns MediaStoreInfo()

        repository
            .getMediaStoreInfo()
            .test()
            .assertNoErrors()
            .assertValue(MediaStoreInfo())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get media store info, gateway throws exception, expected error value`() {
        every {
            stubMediaStoreGateway.getInfo()
        } throws stubException

        repository
            .getMediaStoreInfo()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get by id, local returns data, expected valid domain model value`() {
        every {
            stubLocalDataSource.queryById(any())
        } returns Single.just(mockAiGenerationResult)

        repository
            .getById(5598L)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get by id, local throws exception, expected error value`() {
        every {
            stubLocalDataSource.queryById(any())
        } returns Single.error(stubException)

        repository
            .getById(5598L)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to delete by id list, local delete success, expected complete value`() {
        every {
            stubLocalDataSource.deleteByIdList(any())
        } returns Completable.complete()

        repository
            .deleteByIdList(listOf(5598L, 151297L))
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to delete by id list, local delete fails, expected error value`() {
        every {
            stubLocalDataSource.deleteByIdList(any())
        } returns Completable.error(stubException)

        repository
            .deleteByIdList(listOf(5598L, 151297L))
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to delete by id, local delete success, expected complete value`() {
        every {
            stubLocalDataSource.deleteById(any())
        } returns Completable.complete()

        repository
            .deleteById(5598L)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to delete by id, local delete fails, expected error value`() {
        every {
            stubLocalDataSource.deleteById(any())
        } returns Completable.error(stubException)

        repository
            .deleteById(5598L)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to delete all, local delete success, expected complete value`() {
        every {
            stubLocalDataSource.deleteAll()
        } returns Completable.complete()

        repository
            .deleteAll()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to delete all, local delete fails, expected complete value`() {
        every {
            stubLocalDataSource.deleteAll()
        } returns Completable.error(stubException)

        repository
            .deleteAll()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert data, local insert success, expected id of inserted model value`() {
        every {
            stubPreferenceManager.saveToMediaStore
        } returns false

        every {
            stubLocalDataSource.insert(any())
        } returns Single.just(mockAiGenerationResult.id)

        repository
            .insert(mockAiGenerationResult)
            .test()
            .assertNoErrors()
            .assertValue(5598L)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert data, local insert fails, expected error value`() {
        every {
            stubPreferenceManager.saveToMediaStore
        } returns false

        every {
            stubLocalDataSource.insert(any())
        } returns Single.error(stubException)

        repository
            .insert(mockAiGenerationResult)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
