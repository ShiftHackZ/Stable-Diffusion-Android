package com.shifthackz.aisdv1.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionSamplers
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StableDiffusionModelsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<StableDiffusionModelsDataSource.Remote>()
    private val stubLocalDataSource = mockk<StableDiffusionModelsDataSource.Local>()
    
    private val repository = StableDiffusionModelsRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
    )

    @Test
    fun `given attempt to fetch models, remote returns data, local insert success, expected complete value`() {
        every {
            stubRemoteDataSource.fetchSdModels()
        } returns Single.just(mockStableDiffusionModels)

        every {
            stubLocalDataSource.insertModels(any())
        } returns Completable.complete()

        repository
            .fetchModels()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models, remote throws exception, local insert success, expected error value`() {
        every {
            stubRemoteDataSource.fetchSdModels()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertModels(any())
        } returns Completable.complete()

        repository
            .fetchModels()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch models, remote returns data, local insert fails, expected error value`() {
        every {
            stubRemoteDataSource.fetchSdModels()
        } returns Single.just(mockStableDiffusionModels)

        every {
            stubLocalDataSource.insertModels(any())
        } returns Completable.error(stubException)

        repository
            .fetchModels()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get models, local data source returns list, expected valid domain models list value`() {
        every {
            stubLocalDataSource.getModels()
        } returns Single.just(mockStableDiffusionModels)

        repository
            .getModels()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get models, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLocalDataSource.getModels()
        } returns Single.just(emptyList())

        repository
            .getModels()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get models, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.getModels()
        } returns Single.error(stubException)

        repository
            .getModels()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchSdModels()
        } returns Single.just(mockStableDiffusionModels)

        every {
            stubLocalDataSource.insertModels(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getModels()
        } returns Single.just(mockStableDiffusionModels)

        repository
            .fetchAndGetModels()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchSdModels()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertModels(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getModels()
        } returns Single.just(mockStableDiffusionModels)

        repository
            .fetchAndGetModels()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local fails, expected valid error value`() {
        every {
            stubRemoteDataSource.fetchSdModels()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.getModels()
        } returns Single.error(stubException)

        repository
            .fetchAndGetModels()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
