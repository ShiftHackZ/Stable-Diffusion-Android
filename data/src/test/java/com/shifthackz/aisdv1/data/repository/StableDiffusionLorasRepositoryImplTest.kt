package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionLoras
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionLorasDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StableDiffusionLorasRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<StableDiffusionLorasDataSource.Remote>()
    private val stubLocalDataSource = mockk<StableDiffusionLorasDataSource.Local>()
    
    private val repository = StableDiffusionLorasRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
    )

    @Test
    fun `given attempt to fetch loras, remote returns data, local insert success, expected complete value`() {
        every {
            stubRemoteDataSource.fetchLoras()
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLocalDataSource.insertLoras(any())
        } returns Completable.complete()

        repository
            .fetchLoras()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch loras, remote throws exception, local insert success, expected error value`() {
        every {
            stubRemoteDataSource.fetchLoras()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertLoras(any())
        } returns Completable.complete()

        repository
            .fetchLoras()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch loras, remote returns data, local insert fails, expected error value`() {
        every {
            stubRemoteDataSource.fetchLoras()
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLocalDataSource.insertLoras(any())
        } returns Completable.error(stubException)

        repository
            .fetchLoras()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get loras, local data source returns list, expected valid domain models list value`() {
        every {
            stubLocalDataSource.getLoras()
        } returns Single.just(mockStableDiffusionLoras)

        repository
            .getLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get loras, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLocalDataSource.getLoras()
        } returns Single.just(emptyList())

        repository
            .getLoras()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get loras, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.getLoras()
        } returns Single.error(stubException)

        repository
            .getLoras()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchLoras()
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLocalDataSource.insertLoras(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getLoras()
        } returns Single.just(mockStableDiffusionLoras)

        repository
            .fetchAndGetLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchLoras()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertLoras(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getLoras()
        } returns Single.just(mockStableDiffusionLoras)

        repository
            .fetchAndGetLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, remote fails, local fails, expected valid error value`() {
        every {
            stubRemoteDataSource.fetchLoras()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.getLoras()
        } returns Single.error(stubException)

        repository
            .fetchAndGetLoras()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
