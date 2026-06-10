package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockEmptyKtorSdEmbeddingsResponse
import com.shifthackz.aisdv1.data.mocks.mockKtorSdEmbeddingsResponse
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KtorStableDiffusionEmbeddingsRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<Automatic1111MetadataApi>()

    private val remoteDataSource = KtorStableDiffusionEmbeddingsRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch embeddings, api returns success response, expected valid embeddings value`() = runTest {
        coEvery {
            stubApi.fetchEmbeddings(BASE_URL, null)
        } returns mockKtorSdEmbeddingsResponse

        val actual = remoteDataSource.fetchEmbeddings(BASE_URL, AuthorizationCredentials.None)

        Assert.assertEquals(listOf(Embedding("1504")), actual)
    }

    @Test
    fun `given attempt to fetch embeddings, api returns empty response, expected empty embeddings value`() = runTest {
        coEvery {
            stubApi.fetchEmbeddings(BASE_URL, null)
        } returns mockEmptyKtorSdEmbeddingsResponse

        val actual = remoteDataSource.fetchEmbeddings(BASE_URL, AuthorizationCredentials.None)

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to fetch embeddings, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchEmbeddings(BASE_URL, null)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetchEmbeddings(BASE_URL, AuthorizationCredentials.None)
        }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val BASE_URL = "http://192.168.0.1:7860"
    }
}
