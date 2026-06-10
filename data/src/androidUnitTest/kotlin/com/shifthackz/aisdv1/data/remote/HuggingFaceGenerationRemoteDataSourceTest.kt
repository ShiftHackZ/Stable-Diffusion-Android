package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceGenerationApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class HuggingFaceGenerationRemoteDataSourceTest {

    private val stubApiException = Throwable("Internal server error.")
    private val stubApi = mockk<HuggingFaceGenerationApi>()

    private val remoteDataSource = KtorHuggingFaceGenerationRemoteDataSource(
        api = stubApi,
    )

    @Test
    fun `given attempt to validate api key, api request succeeds, expected true`() = runTest {
        coEvery {
            stubApi.validateBearerToken(API_KEY)
        } returns Unit

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertTrue(actual)
    }

    @Test
    fun `given attempt to validate api key, api request fails, expected false`() = runTest {
        coEvery {
            stubApi.validateBearerToken(API_KEY)
        } throws Throwable("Invalid api key.")

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertFalse(actual)
    }

    @Test
    fun `given attempt to generate txt2img, api returns image bytes, expected valid ai generation result value`() = runTest {
        coEvery {
            stubApi.generate(API_KEY, MODEL, any())
        } returns IMAGE_BYTES

        val actual = remoteDataSource.textToImage(API_KEY, MODEL, mockTextToImagePayload)

        Assert.assertEquals(IMAGE_BASE_64, actual.image)
        Assert.assertEquals(AiGenerationResult.Type.TEXT_TO_IMAGE, actual.type)
    }

    @Test
    fun `given attempt to generate txt2img, api fails, expected error value`() = runTest {
        coEvery {
            stubApi.generate(API_KEY, MODEL, any())
        } throws stubApiException

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, MODEL, mockTextToImagePayload)
        }

        Assert.assertSame(stubApiException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate img2img, api returns image bytes, expected valid ai generation result value`() = runTest {
        coEvery {
            stubApi.generate(API_KEY, MODEL, any())
        } returns IMAGE_BYTES

        val actual = remoteDataSource.imageToImage(API_KEY, MODEL, mockImageToImagePayload)

        Assert.assertEquals(IMAGE_BASE_64, actual.image)
        Assert.assertEquals(AiGenerationResult.Type.IMAGE_TO_IMAGE, actual.type)
    }

    @Test
    fun `given attempt to generate img2img, api fails, expected error value`() = runTest {
        coEvery {
            stubApi.generate(API_KEY, MODEL, any())
        } throws stubApiException

        val actual = runCatching {
            remoteDataSource.imageToImage(API_KEY, MODEL, mockImageToImagePayload)
        }

        Assert.assertSame(stubApiException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "hf_5598"
        const val MODEL = "model"
        const val IMAGE_BASE_64 = "aW1hZ2U="
        val IMAGE_BYTES = "image".encodeToByteArray()
    }
}
