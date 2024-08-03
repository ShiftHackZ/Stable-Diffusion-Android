package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockServerConfigurationRaw
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class ServerConfigurationRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubUrlProvider = mockk<ServerUrlProvider>()
    private val stubApi = mockk<Automatic1111RestApi>()

    private val remoteDataSource = ServerConfigurationRemoteDataSource(
        serverUrlProvider = stubUrlProvider,
        api = stubApi,
    )

    @Before
    fun initialize() {
        every {
            stubUrlProvider(any())
        } returns Single.just("http://192.168.0.1:7860")
    }

    @Test
    fun `given attempt to fetch configuration, api returns success response, expected valid server configuration value`() {
        every {
            stubApi.fetchConfiguration(any())
        } returns Single.just(mockServerConfigurationRaw)

        remoteDataSource
            .fetchConfiguration()
            .test()
            .assertNoErrors()
            .assertValue(ServerConfiguration("5598"))
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch configuration, api returns error response, expected error value`() {
        every {
            stubApi.fetchConfiguration(any())
        } returns Single.error(stubException)

        remoteDataSource
            .fetchConfiguration()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to update configuration, api returns success response, expected complete value`() {
        every {
            stubApi.updateConfiguration(any(), any())
        } returns Completable.complete()

        remoteDataSource
            .updateConfiguration(ServerConfiguration("5598"))
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to update configuration, api returns error response, expected error value`() {
        every {
            stubApi.updateConfiguration(any(), any())
        } returns Completable.error(stubException)

        remoteDataSource
            .updateConfiguration(ServerConfiguration("5598"))
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
