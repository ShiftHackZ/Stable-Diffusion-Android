package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockServerConfiguration
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class ServerConfigurationRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<ServerConfigurationDataSource.Remote>()
    private val stubLocalDataSource = mockk<ServerConfigurationDataSource.Local>()

    private val repository = ServerConfigurationRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
    )

    @Test
    fun `given attempt to update configuration, remote completes, expected complete value`() {
        every {
            stubRemoteDataSource.updateConfiguration(any())
        } returns Completable.complete()

        repository
            .updateConfiguration(mockServerConfiguration)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to update configuration, remote throws exception, expected error value`() {
        every {
            stubRemoteDataSource.updateConfiguration(any())
        } returns Completable.error(stubException)

        repository
            .updateConfiguration(mockServerConfiguration)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get configuration, local returns data, expected valid domain model value`() {
        every {
            stubLocalDataSource.get()
        } returns Single.just(mockServerConfiguration)

        repository
            .getConfiguration()
            .test()
            .assertNoErrors()
            .assertValue(mockServerConfiguration)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get configuration, local throws exception, expected error value`() {
        every {
            stubLocalDataSource.get()
        } returns Single.error(stubException)

        repository
            .getConfiguration()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `attempt to fetch configuration, remote returns data, local save success, expected complete value`() {
        every {
            stubRemoteDataSource.fetchConfiguration()
        } returns Single.just(mockServerConfiguration)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetchConfiguration()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `attempt to fetch configuration, remote throws exception, expected error value`() {
        every {
            stubRemoteDataSource.fetchConfiguration()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        repository
            .fetchConfiguration()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `attempt to fetch configuration, remote returns data, local save fails, expected error value`() {
        every {
            stubRemoteDataSource.fetchConfiguration()
        } returns Single.just(mockServerConfiguration)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.error(stubException)

        repository
            .fetchConfiguration()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get, fetch success, get success, expected valid domain model value`() {
        every {
            stubRemoteDataSource.fetchConfiguration()
        } returns Single.just(mockServerConfiguration)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.get()
        } returns Single.just(mockServerConfiguration)

        repository
            .fetchAndGetConfiguration()
            .test()
            .assertNoErrors()
            .assertValue(mockServerConfiguration)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get, fetch fails, get success, expected valid domain model value`() {
        every {
            stubRemoteDataSource.fetchConfiguration()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.get()
        } returns Single.just(mockServerConfiguration)

        repository
            .fetchAndGetConfiguration()
            .test()
            .assertNoErrors()
            .assertValue(mockServerConfiguration)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get, fetch fails, get fails, expected error value`() {
        every {
            stubRemoteDataSource.fetchConfiguration()
        } returns Single.error(stubException)

        every {
            stubLocalDataSource.save(any())
        } returns Completable.complete()

        every {
            stubLocalDataSource.get()
        } returns Single.error(stubException)

        repository
            .fetchAndGetConfiguration()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
