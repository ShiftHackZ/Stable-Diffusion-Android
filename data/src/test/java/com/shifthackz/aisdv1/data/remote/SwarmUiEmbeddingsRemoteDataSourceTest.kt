package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockSwarmUiModelsRaw
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.response.SwarmUiModelsResponse
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class SwarmUiEmbeddingsRemoteDataSourceTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubServerUrlProvider = mockk<ServerUrlProvider>()
    private val stubApi = mockk<SwarmUiApi>()
    
    private val remoteDataSource = SwarmUiEmbeddingsRemoteDataSource(
        serverUrlProvider = stubServerUrlProvider,
        api = stubApi,
    )

    @Before
    fun initialize() {
        every {
            stubServerUrlProvider(any())
        } returns Single.just("http://192.168.0.1:7801")
    }

    @Test
    fun `given attempt to fetch models, api returns success response, expected valid models list value`() {
        every {
            stubApi.fetchModels(any(), any())
        } returns Single.just(SwarmUiModelsResponse(mockSwarmUiModelsRaw))

        remoteDataSource
            .fetchEmbeddings("5598")
            .test()
            .assertNoErrors()
            .assertValue { models ->
                models is List<Embedding>
                        && models.size == mockSwarmUiModelsRaw.size
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models, api returns empty response, expected empty models value`() {
        every {
            stubApi.fetchModels(any(), any())
        } returns Single.just(SwarmUiModelsResponse(emptyList()))

        remoteDataSource
            .fetchEmbeddings("5598")
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models, api returns error response, expected error value`() {
        every {
            stubApi.fetchModels(any(), any())
        } returns Single.error(stubException)

        remoteDataSource
            .fetchEmbeddings("5598")
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
