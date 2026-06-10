package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockKtorStableDiffusionHyperNetworkRaw
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KtorStableDiffusionHyperNetworksRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<Automatic1111MetadataApi>()

    private val remoteDataSource = KtorStableDiffusionHyperNetworksRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch hyper networks, api returns success response, expected valid hyper networks list value`() =
        runTest {
            coEvery {
                stubApi.fetchHyperNetworks(BASE_URL, null)
            } returns mockKtorStableDiffusionHyperNetworkRaw

            val actual = remoteDataSource.fetchHyperNetworks(BASE_URL, AuthorizationCredentials.None)

            Assert.assertEquals(mockKtorStableDiffusionHyperNetworkRaw.size, actual.size)
        }

    @Test
    fun `given attempt to fetch hyper networks, api returns empty response, expected empty hyper networks list value`() =
        runTest {
            coEvery {
                stubApi.fetchHyperNetworks(BASE_URL, null)
            } returns emptyList()

            val actual = remoteDataSource.fetchHyperNetworks(BASE_URL, AuthorizationCredentials.None)

            Assert.assertEquals(emptyList<Any>(), actual)
        }

    @Test
    fun `given attempt to fetch hyper networks, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchHyperNetworks(BASE_URL, null)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetchHyperNetworks(BASE_URL, AuthorizationCredentials.None)
        }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val BASE_URL = "http://192.168.0.1:7860"
    }
}
