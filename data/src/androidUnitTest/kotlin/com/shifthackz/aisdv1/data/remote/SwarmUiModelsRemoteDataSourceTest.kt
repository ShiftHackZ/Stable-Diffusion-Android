package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockKtorSwarmUiModelsRaw
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiModelsApi
import com.shifthackz.aisdv1.network.response.KtorSwarmUiModelsResponse
import com.shifthackz.aisdv1.network.response.KtorSwarmUiSessionResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KtorSwarmUiModelsRemoteDataSourceTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubApi = mockk<SwarmUiModelsApi>()

    private val remoteDataSource = KtorSwarmUiModelsRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch new session, api returns success response, expected valid session value`() = runTest {
        coEvery {
            stubApi.getNewSession(BASE_URL, null)
        } returns KtorSwarmUiSessionResponse(SESSION_ID)

        val actual = remoteDataSource.getNewSession(BASE_URL, AuthorizationCredentials.None)

        Assert.assertEquals(SESSION_ID, actual)
    }

    @Test
    fun `given attempt to fetch new session, api returns blank session, expected error value`() = runTest {
        coEvery {
            stubApi.getNewSession(BASE_URL, null)
        } returns KtorSwarmUiSessionResponse("")

        val actual = runCatching {
            remoteDataSource.getNewSession(BASE_URL, AuthorizationCredentials.None)
        }

        Assert.assertTrue(actual.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `given attempt to fetch models, api returns success response, expected valid models list value`() = runTest {
        coEvery {
            stubApi.fetchModels(BASE_URL, any(), null)
        } returns KtorSwarmUiModelsResponse(mockKtorSwarmUiModelsRaw)

        val actual = remoteDataSource.fetchSwarmModels(
            baseUrl = BASE_URL,
            sessionId = SESSION_ID,
            credentials = AuthorizationCredentials.None,
        )

        Assert.assertEquals(mockKtorSwarmUiModelsRaw.size, actual.size)
    }

    @Test
    fun `given attempt to fetch models, api returns empty response, expected empty models value`() = runTest {
        coEvery {
            stubApi.fetchModels(BASE_URL, any(), null)
        } returns KtorSwarmUiModelsResponse(emptyList())

        val actual = remoteDataSource.fetchSwarmModels(
            baseUrl = BASE_URL,
            sessionId = SESSION_ID,
            credentials = AuthorizationCredentials.None,
        )

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to fetch models, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchModels(BASE_URL, any(), null)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetchSwarmModels(
                baseUrl = BASE_URL,
                sessionId = SESSION_ID,
                credentials = AuthorizationCredentials.None,
            )
        }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val BASE_URL = "http://192.168.0.1:7801"
        const val SESSION_ID = "5598"
    }
}
