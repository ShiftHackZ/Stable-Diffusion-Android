package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionHyperNetworks
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StableDiffusionHyperNetworksRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<StableDiffusionHyperNetworksDataSource.Remote>()
    private val stubLocalDataSource = mockk<StableDiffusionHyperNetworksDataSource.Local>()
    
    private val repository = StableDiffusionHyperNetworksRepositoryImpl(
        stubRemoteDataSource,
        stubLocalDataSource,
    )

    @Test
    fun `given attempt to fetch hyper networks, remote returns data, local insert success, expected complete value`() {
        every {
            stubRemoteDataSource.fetchHyperNetworks()
        } returns Single.just(mockStableDiffusionHyperNetworks)

        every {
            stubLocalDataSource.insertHyperNetworks(any())
        } returns Completable.complete()

        repository
            .fetchHyperNetworks()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch hyper networks, remote throws exception, local insert success, expected error value`() {
        every {
            stubRemoteDataSource.fetchHyperNetworks()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertHyperNetworks(any())
        } returns Completable.complete()

        repository
            .fetchHyperNetworks()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch hyper networks, remote returns data, local insert fails, expected error value`() {
        every {
            stubRemoteDataSource.fetchHyperNetworks()
        } returns Single.just(mockStableDiffusionHyperNetworks)

        every {
            stubLocalDataSource.insertHyperNetworks(any())
        } returns Completable.error(stubException)

        repository
            .fetchHyperNetworks()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get hyper networks, local data source returns list, expected valid domain models list value`() {
        every {
            stubLocalDataSource.getHyperNetworks()
        } returns Single.just(mockStableDiffusionHyperNetworks)

        repository
            .getHyperNetworks()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionHyperNetworks)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get hyper networks, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLocalDataSource.getHyperNetworks()
        } returns Single.just(emptyList())

        repository
            .getHyperNetworks()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get hyper networks, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.getHyperNetworks()
        } returns Single.error(stubException)

        repository
            .getHyperNetworks()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get hyper networks, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchHyperNetworks()
        } returns Single.just(mockStableDiffusionHyperNetworks)

        every {
            stubLocalDataSource.insertHyperNetworks(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getHyperNetworks()
        } returns Single.just(mockStableDiffusionHyperNetworks)

        repository
            .fetchAndGetHyperNetworks()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionHyperNetworks)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get hyper networks, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubRemoteDataSource.fetchHyperNetworks()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertHyperNetworks(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.getHyperNetworks()
        } returns Single.just(mockStableDiffusionHyperNetworks)

        repository
            .fetchAndGetHyperNetworks()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionHyperNetworks)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get hyper networks, remote fails, local fails, expected valid error value`() {
        every {
            stubRemoteDataSource.fetchHyperNetworks()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.getHyperNetworks()
        } returns Single.error(stubException)

        repository
            .fetchAndGetHyperNetworks()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
