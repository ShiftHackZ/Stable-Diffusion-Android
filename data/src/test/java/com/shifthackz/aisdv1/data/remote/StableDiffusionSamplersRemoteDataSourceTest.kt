package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionSamplerRaw
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class StableDiffusionSamplersRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubUrlProvider = mockk<ServerUrlProvider>()
    private val stubApi = mockk<Automatic1111RestApi>()

    private val remoteDataSource = StableDiffusionSamplersRemoteDataSource(
        stubUrlProvider,
        stubApi,
    )

    @Before
    fun initialize() {
        every {
            stubUrlProvider(any())
        } returns Single.just("http://192.168.0.1:7860")
    }

    @Test
    fun `given attempt to fetch samplers, api returns success response, expected valid samplers list value`() {
        every {
            stubApi.fetchSamplers(any())
        } returns Single.just(mockStableDiffusionSamplerRaw)

        remoteDataSource
            .fetchSamplers()
            .test()
            .assertNoErrors()
            .assertValue { samplers ->
                samplers is List<StableDiffusionSampler>
                        && samplers.size == mockStableDiffusionSamplerRaw.size
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch samplers, api returns empty response, expected empty samplers value`() {
        every {
            stubApi.fetchSamplers(any())
        } returns Single.just(emptyList())

        remoteDataSource
            .fetchSamplers()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch samplers, api returns error response, expected error value`() {
        every {
            stubApi.fetchSamplers(any())
        } returns Single.error(stubException)

        remoteDataSource
            .fetchSamplers()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
