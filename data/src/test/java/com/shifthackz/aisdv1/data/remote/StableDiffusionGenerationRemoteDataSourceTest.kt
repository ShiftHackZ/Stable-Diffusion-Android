package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockSdGenerationResponse
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class StableDiffusionGenerationRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubUrlProvider = mockk<ServerUrlProvider>()
    private val stubApi = mockk<Automatic1111RestApi>()

    private val remoteDataSource = StableDiffusionGenerationRemoteDataSource(
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
    fun `given attempt to do health check, api returns success response, expected complete value`() {
        every {
            stubApi.healthCheck(any())
        } returns Completable.complete()

        remoteDataSource
            .checkAvailability()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to do health check, api returns error response, expected error value`() {
        every {
            stubApi.healthCheck(any())
        } returns Completable.error(stubException)

        remoteDataSource
            .checkAvailability()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() {
        every {
            stubApi.textToImage(any(), any())
        } returns Single.just(mockSdGenerationResponse)

        remoteDataSource
            .textToImage(mockTextToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue { it is AiGenerationResult }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate txt2img, api returns error, expected error value`() {
        every {
            stubApi.textToImage(any(), any())
        } returns Single.error(stubException)

        remoteDataSource
            .textToImage(mockTextToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate img2img, api returns result, expected valid ai generation result value`() {
        every {
            stubApi.imageToImage(any(), any())
        } returns Single.just(mockSdGenerationResponse)

        remoteDataSource
            .imageToImage(mockImageToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue { it is AiGenerationResult }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate img2img, api returns error, expected error value`() {
        every {
            stubApi.imageToImage(any(), any())
        } returns Single.error(stubException)

        remoteDataSource
            .imageToImage(mockImageToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to interrupt generation, api returns success response, expected complete value`() {
        every {
            stubApi.interrupt(any())
        } returns Completable.complete()

        remoteDataSource
            .interruptGeneration()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to interrupt generation, api returns error response, expected error value`() {
        every {
            stubApi.interrupt(any())
        } returns Completable.error(stubException)

        remoteDataSource
            .interruptGeneration()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
