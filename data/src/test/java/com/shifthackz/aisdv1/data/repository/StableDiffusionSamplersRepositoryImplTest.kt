package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionSamplers
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StableDiffusionSamplersRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<StableDiffusionSamplersDataSource.Remote>()
    private val stubLocalDataSource = mockk<StableDiffusionSamplersDataSource.Local>()

    private val repository = StableDiffusionSamplersRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
    )

    @Test
    fun `given attempt to fetch samplers, remote returns data, local insert success, expected complete value`() {
        every {
            stubRemoteDataSource.fetchSamplers()
        } returns Single.just(mockStableDiffusionSamplers)

        every {
            stubLocalDataSource.insertSamplers(any())
        } returns Completable.complete()

        repository
            .fetchSamplers()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch samplers, remote throws exception, local insert success, expected error value`() {
        every {
            stubRemoteDataSource.fetchSamplers()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.insertSamplers(any())
        } returns Completable.complete()

        repository
            .fetchSamplers()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch samplers, remote returns data, local insert fails, expected error value`() {
        every {
            stubRemoteDataSource.fetchSamplers()
        } returns Single.just(mockStableDiffusionSamplers)

        every {
            stubLocalDataSource.insertSamplers(any())
        } returns Completable.error(stubException)

        repository
            .fetchSamplers()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get samplers, local data source returns list, expected valid domain models list value`() {
        every {
            stubLocalDataSource.getSamplers()
        } returns Single.just(mockStableDiffusionSamplers)

        repository
            .getSamplers()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionSamplers)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get samplers, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLocalDataSource.getSamplers()
        } returns Single.just(emptyList())

        repository
            .getSamplers()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get samplers, local data source throws exception, expected error value`() {
        every {
            stubLocalDataSource.getSamplers()
        } returns Single.error(stubException)

        repository
            .getSamplers()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
