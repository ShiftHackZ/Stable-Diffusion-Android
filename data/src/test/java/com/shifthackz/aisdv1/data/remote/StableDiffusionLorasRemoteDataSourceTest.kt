package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionLoraRaw
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class StableDiffusionLorasRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubUrlProvider = mockk<ServerUrlProvider>()
    private val stubApi = mockk<Automatic1111RestApi>()

    private val remoteDataSource = StableDiffusionLorasRemoteDataSource(
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
    fun `given attempt to fetch loras, api returns success response, expected valid loras list value`() {
        every {
            stubApi.fetchLoras(any())
        } returns Single.just(mockStableDiffusionLoraRaw)

        remoteDataSource
            .fetchLoras()
            .test()
            .assertNoErrors()
            .assertValue { loras ->
                loras is List<LoRA>
                        && loras.size == mockStableDiffusionLoraRaw.size
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch loras, api returns empty response, expected empty loras value`() {
        every {
            stubApi.fetchLoras(any())
        } returns Single.just(emptyList())

        remoteDataSource
            .fetchLoras()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch loras, api returns error response, expected error value`() {
        every {
            stubApi.fetchLoras(any())
        } returns Single.error(stubException)

        remoteDataSource
            .fetchLoras()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
