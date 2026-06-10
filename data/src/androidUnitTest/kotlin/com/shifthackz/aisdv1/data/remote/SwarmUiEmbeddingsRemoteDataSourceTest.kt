package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockKtorSwarmUiModelsRaw
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiModelsApi
import com.shifthackz.aisdv1.network.model.SwarmUiModelRaw
import com.shifthackz.aisdv1.network.response.KtorSwarmUiModelsResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KtorSwarmUiEmbeddingsRemoteDataSourceTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubApi = mockk<SwarmUiModelsApi>()

    private val remoteDataSource = KtorSwarmUiEmbeddingsRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch embeddings, api returns success response, expected valid embeddings list value`() = runTest {
        coEvery {
            stubApi.fetchModels(BASE_URL, any(), null)
        } returns KtorSwarmUiModelsResponse(mockKtorSwarmUiModelsRaw)

        val actual = remoteDataSource.fetchEmbeddings(BASE_URL, SESSION_ID, AuthorizationCredentials.None)

        Assert.assertEquals(listOf(Embedding("5598")), actual)
    }

    @Test
    fun `given swarm embedding has no title, expected keyword mapped from file name`() = runTest {
        coEvery {
            stubApi.fetchModels(BASE_URL, any(), null)
        } returns KtorSwarmUiModelsResponse(
            files = listOf(
                SwarmUiModelRaw(
                    name = "cute-cat.bin",
                    title = null,
                ),
            ),
        )

        val actual = remoteDataSource.fetchEmbeddings(BASE_URL, SESSION_ID, AuthorizationCredentials.None)

        Assert.assertEquals(listOf(Embedding("cute-cat")), actual)
    }

    @Test
    fun `given attempt to fetch embeddings, api returns empty response, expected empty embeddings value`() = runTest {
        coEvery {
            stubApi.fetchModels(BASE_URL, any(), null)
        } returns KtorSwarmUiModelsResponse(emptyList())

        val actual = remoteDataSource.fetchEmbeddings(BASE_URL, SESSION_ID, AuthorizationCredentials.None)

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to fetch embeddings, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchModels(BASE_URL, any(), null)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetchEmbeddings(BASE_URL, SESSION_ID, AuthorizationCredentials.None)
        }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val BASE_URL = "http://192.168.0.1:7801"
        const val SESSION_ID = "5598"
    }
}
