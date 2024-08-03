package com.shifthackz.aisdv1.data.remote

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceApi
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceInferenceApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class HuggingFaceGenerationRemoteDataSourceTest {

    private val stubConverterException = Throwable("Converter failure.")
    private val stubApiException = Throwable("Internal server error.")
    private val stubBitmap = mockk<Bitmap>()
    private val stubApi = mockk<HuggingFaceApi>()
    private val stubInferenceApi = mockk<HuggingFaceInferenceApi>()
    private val stubBmpToBase64Converter = mockk<BitmapToBase64Converter>()

    private val remoteDataSource = HuggingFaceGenerationRemoteDataSource(
        huggingFaceApi = stubApi,
        huggingFaceInferenceApi = stubInferenceApi,
        converter = stubBmpToBase64Converter,
    )

    @Test
    fun `given attempt to validate api key, api request succeeds, expected true`() {
        every {
            stubApi.validateBearerToken()
        } returns Completable.complete()

        remoteDataSource
            .validateApiKey()
            .test()
            .assertNoErrors()
            .assertValue(true)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to validate api key, api request fails, expected false`() {
        every {
            stubApi.validateBearerToken()
        } returns Completable.error(Throwable("Invalid api key."))

        remoteDataSource
            .validateApiKey()
            .test()
            .assertNoErrors()
            .assertValue(false)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate txt2img, api returns result, converter proceed successfully, expected valid ai generation result value`() {
        every {
            stubInferenceApi.generate(any(), any())
        } returns Single.just(stubBitmap)

        every {
            stubBmpToBase64Converter(any())
        } returns Single.just(BitmapToBase64Converter.Output("base64"))

        remoteDataSource
            .textToImage("model", mockTextToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue { it is AiGenerationResult }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate txt2img, api fails, expected error value`() {
        every {
            stubInferenceApi.generate(any(), any())
        } returns Single.error(stubApiException)

        remoteDataSource
            .textToImage("model", mockTextToImagePayload)
            .test()
            .assertError(stubApiException)
            .assertValueCount(0)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate txt2img, api returns result, converter proceed with fail, expected error value`() {
        every {
            stubInferenceApi.generate(any(), any())
        } returns Single.just(stubBitmap)

        every {
            stubBmpToBase64Converter(any())
        } returns Single.error(stubConverterException)

        remoteDataSource
            .textToImage("model", mockTextToImagePayload)
            .test()
            .assertError(stubConverterException)
            .assertValueCount(0)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate img2img, api returns result, converter proceed successfully, expected valid ai generation result value`() {
        every {
            stubInferenceApi.generate(any(), any())
        } returns Single.just(stubBitmap)

        every {
            stubBmpToBase64Converter(any())
        } returns Single.just(BitmapToBase64Converter.Output("base64"))

        remoteDataSource
            .imageToImage("model", mockImageToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue { it is AiGenerationResult }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate img2img, api fails, expected error value`() {
        every {
            stubInferenceApi.generate(any(), any())
        } returns Single.error(stubApiException)

        remoteDataSource
            .imageToImage("model", mockImageToImagePayload)
            .test()
            .assertError(stubApiException)
            .assertValueCount(0)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate img2img, api returns result, converter proceed with fail, expected error value`() {
        every {
            stubInferenceApi.generate(any(), any())
        } returns Single.just(stubBitmap)

        every {
            stubBmpToBase64Converter(any())
        } returns Single.error(stubConverterException)

        remoteDataSource
            .imageToImage("model", mockImageToImagePayload)
            .test()
            .assertError(stubConverterException)
            .assertValueCount(0)
            .await()
            .assertNotComplete()
    }
}
