package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi
import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServerConfigurationRemoteDataSourceTest {

    private val stubBaseUrl = "http://192.168.0.1:7860"
    private val stubCredentials = AuthorizationCredentials.HttpBasic("user", "password")
    private val stubAuthorization = BasicHttpAuthorization("user", "password")
    private val stubException = RuntimeException("Internal server error.")
    private val stubApi = mockk<Automatic1111MetadataApi>()

    private val remoteDataSource = KtorServerConfigurationRemoteDataSource(
        api = stubApi,
    )

    @Test
    fun `given attempt to fetch configuration, api returns success response, expected valid server configuration value`() = runTest {
        coEvery {
            stubApi.fetchConfiguration(stubBaseUrl, stubAuthorization)
        } returns ServerConfigurationRaw("5598")

        val actual = remoteDataSource.fetchConfiguration(stubBaseUrl, stubCredentials)

        assertEquals(ServerConfiguration("5598"), actual)
    }

    @Test
    fun `given attempt to fetch configuration, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchConfiguration(stubBaseUrl, stubAuthorization)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetchConfiguration(stubBaseUrl, stubCredentials)
        }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to update configuration, api returns success response, expected complete value`() = runTest {
        coEvery {
            stubApi.updateConfiguration(
                baseUrl = stubBaseUrl,
                authorization = stubAuthorization,
                request = ServerConfigurationRaw("5598"),
            )
        } returns Unit

        val actual = runCatching {
            remoteDataSource.updateConfiguration(stubBaseUrl, stubCredentials, ServerConfiguration("5598"))
        }

        assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to update configuration, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.updateConfiguration(
                baseUrl = stubBaseUrl,
                authorization = stubAuthorization,
                request = ServerConfigurationRaw("5598"),
            )
        } throws stubException

        val actual = runCatching {
            remoteDataSource.updateConfiguration(stubBaseUrl, stubCredentials, ServerConfiguration("5598"))
        }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
