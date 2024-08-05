package com.shifthackz.aisdv1.data.remote

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64EncodingConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockSwarmUiGenerationResponse
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class SwarmUiGenerationRemoteDataSourceTest {

    private val stubBitmap = mockk<Bitmap>()
    private val stubException = Throwable("Something went wrong.")
    private val stubServerUrlProvider = mockk<ServerUrlProvider>()
    private val stubApi = mockk<SwarmUiApi>()
    private val stubBmpToBase64Converter = mockk<BitmapToBase64Converter>()
    private val stubBase64EncConverter = mockk<Base64EncodingConverter>()

    private val remoteDataSource = SwarmUiGenerationRemoteDataSource(
        serverUrlProvider = stubServerUrlProvider,
        api = stubApi,
        bmpToBase64Converter = stubBmpToBase64Converter,
        base64EncodingConverter = stubBase64EncConverter,
    )

    @Before
    fun initialize() {
        every {
            stubServerUrlProvider(any())
        } returns Single.just("http://192.168.0.1:7801")
    }


    @Test
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() {
        every {
            stubApi.generate(any(), any())
        } returns Single.just(mockSwarmUiGenerationResponse)

        every {
            stubApi.downloadImage(any())
        } returns Single.just(stubBitmap)

        every {
            stubBmpToBase64Converter(any())
        } returns Single.just(BitmapToBase64Converter.Output("base64"))

        remoteDataSource
            .textToImage(SESSION_ID, MODEL, mockTextToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue { it is AiGenerationResult }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate txt2img, api returns error, expected error value`() {
        every {
            stubApi.generate(any(), any())
        } returns Single.error(stubException)

        remoteDataSource
            .textToImage(SESSION_ID, MODEL, mockTextToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate img2img, api returns result, expected valid ai generation result value`() {
        every {
            stubBase64EncConverter(any())
        } returns Single.just(Base64EncodingConverter.Output("base64"))

        every {
            stubApi.generate(any(), any())
        } returns Single.just(mockSwarmUiGenerationResponse)

        every {
            stubApi.downloadImage(any())
        } returns Single.just(stubBitmap)

        every {
            stubBmpToBase64Converter(any())
        } returns Single.just(BitmapToBase64Converter.Output("base64"))

        remoteDataSource
            .imageToImage(SESSION_ID, MODEL, mockImageToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue { it is AiGenerationResult }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate img2img, api returns error, expected error value`() {
        every {
            stubBase64EncConverter(any())
        } returns Single.error(stubException)

        every {
            stubApi.generate(any(), any())
        } returns Single.error(stubException)

        remoteDataSource
            .imageToImage(SESSION_ID, MODEL, mockImageToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
    
    companion object {
        private const val SESSION_ID = "5598"
        private const val MODEL = "OpenStableDiffusion"
    }
}
