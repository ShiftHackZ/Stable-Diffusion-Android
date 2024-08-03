package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class HuggingFaceModelsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<HuggingFaceModelsDataSource.Remote>()
    private val stubLocalDataSource = mockk<HuggingFaceModelsDataSource.Local>()

    private val repository = HuggingFaceModelsRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
    )

    @Test
    fun `given attempt to fetch models, remote returns data, local insert success, expected complete value`() {
        every {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns Single.just(mockHuggingFaceModels)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetchHuggingFaceModels()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models, remote throws exception, local insert success, expected error value`() {
        every {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetchHuggingFaceModels()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch models, remote returns data, local insert fails, expected error value`() {
        every {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns Single.just(mockHuggingFaceModels)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.error(stubException)

        repository
            .fetchHuggingFaceModels()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get models, local data source returns list, expected valid domain models list value`() {
        every {
            stubLocalDataSource.getAll()
        } returns Single.just(mockHuggingFaceModels)

        repository
            .getHuggingFaceModels()
            .test()
            .assertNoErrors()
            .assertValue(mockHuggingFaceModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get models, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLocalDataSource.getAll()
        } returns Single.just(emptyList())

        repository
            .getHuggingFaceModels()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get models, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.getAll()
        } returns Single.error(stubException)

        repository
            .getHuggingFaceModels()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns Single.just(mockHuggingFaceModels)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getAll()
        } returns Single.just(mockHuggingFaceModels)

        repository
            .fetchAndGetHuggingFaceModels()
            .test()
            .assertNoErrors()
            .assertValue(mockHuggingFaceModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getAll()
        } returns Single.just(mockHuggingFaceModels)

        repository
            .fetchAndGetHuggingFaceModels()
            .test()
            .assertNoErrors()
            .assertValue(mockHuggingFaceModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local fails, expected valid error value`() {
        every {
            stubRemoteDataSource.fetchHuggingFaceModels()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.getAll()
        } returns Single.error(stubException)

        repository
            .fetchAndGetHuggingFaceModels()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
