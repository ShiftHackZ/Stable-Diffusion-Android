package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionModelRaw
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class StableDiffusionModelsRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubUrlProvider = mockk<ServerUrlProvider>()
    private val stubApi = mockk<Automatic1111RestApi>()

    private val remoteDataSource = StableDiffusionModelsRemoteDataSource(
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
    fun `given attempt to fetch models, api returns success response, expected valid models list value`() {
        every {
            stubApi.fetchSdModels(any())
        } returns Single.just(mockStableDiffusionModelRaw)

        remoteDataSource
            .fetchSdModels()
            .test()
            .assertNoErrors()
            .assertValue { models ->
                models is List<StableDiffusionModel>
                        && models.size == mockStableDiffusionModelRaw.size
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models, api returns empty response, expected empty models value`() {
        every {
            stubApi.fetchSdModels(any())
        } returns Single.just(emptyList())

        remoteDataSource
            .fetchSdModels()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models, api returns error response, expected error value`() {
        every {
            stubApi.fetchSdModels(any())
        } returns Single.error(stubException)

        remoteDataSource
            .fetchSdModels()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
