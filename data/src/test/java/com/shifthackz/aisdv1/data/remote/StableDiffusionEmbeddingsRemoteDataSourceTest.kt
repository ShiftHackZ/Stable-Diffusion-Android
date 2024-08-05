package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockEmptySdEmbeddingsResponse
import com.shifthackz.aisdv1.data.mocks.mockSdEmbeddingsResponse
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class StableDiffusionEmbeddingsRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubUrlProvider = mockk<ServerUrlProvider>()
    private val stubApi = mockk<Automatic1111RestApi>()

    private val remoteDataSource = StableDiffusionEmbeddingsRemoteDataSource(
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
    fun `given attempt to fetch embeddings, api returns success response, expected valid embeddings value`() {
        every {
            stubApi.fetchEmbeddings(any())
        } returns Single.just(mockSdEmbeddingsResponse)

        remoteDataSource
            .fetchEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(listOf(Embedding("1504")))
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, api returns empty response, expected empty embeddings value`() {
        every {
            stubApi.fetchEmbeddings(any())
        } returns Single.just(mockEmptySdEmbeddingsResponse)

        remoteDataSource
            .fetchEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, api returns error response, expected error value`() {
        every {
            stubApi.fetchEmbeddings(any())
        } returns Single.error(stubException)

        remoteDataSource
            .fetchEmbeddings()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
