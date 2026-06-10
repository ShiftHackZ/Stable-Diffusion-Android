package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockBadStabilityGenerationResponse
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockStabilityGenerationResponse
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiGenerationApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class StabilityAiGenerationRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<StabilityAiGenerationApi>()

    private val remoteDataSource = KtorStabilityAiGenerationRemoteDataSource(
        api = stubApi,
    )

    @Test
    fun `given attempt to validate bearer token, api returns success response, expected true`() = runTest {
        coEvery {
            stubApi.validateBearerToken(API_KEY)
        } returns Unit

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertTrue(actual)
    }

    @Test
    fun `given attempt to validate bearer token, api returns error response, expected false`() = runTest {
        coEvery {
            stubApi.validateBearerToken(API_KEY)
        } throws Throwable("Invalid api key.")

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertFalse(actual)
    }

    @Test
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() = runTest {
        coEvery {
            stubApi.textToImage(API_KEY, ENGINE_ID, any())
        } returns mockStabilityGenerationResponse

        val actual = remoteDataSource.textToImage(API_KEY, ENGINE_ID, mockTextToImagePayload)

        Assert.assertEquals(AiGenerationResult.Type.TEXT_TO_IMAGE, actual.type)
        Assert.assertEquals("base64", actual.image)
    }

    @Test
    fun `given attempt to generate txt2img, api returns empty result, expected error value`() = runTest {
        coEvery {
            stubApi.textToImage(API_KEY, ENGINE_ID, any())
        } returns mockBadStabilityGenerationResponse

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, ENGINE_ID, mockTextToImagePayload)
        }

        Assert.assertTrue(actual.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `given attempt to generate txt2img, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.textToImage(API_KEY, ENGINE_ID, any())
        } throws stubException

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, ENGINE_ID, mockTextToImagePayload)
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate img2img, api returns result, expected valid ai generation result value`() = runTest {
        coEvery {
            stubApi.imageToImage(API_KEY, ENGINE_ID, any(), any())
        } returns mockStabilityGenerationResponse

        val actual = remoteDataSource.imageToImage(
            apiKey = API_KEY,
            engineId = ENGINE_ID,
            payload = mockImageToImagePayload.copy(base64Image = IMAGE_BASE_64),
        )

        Assert.assertEquals(AiGenerationResult.Type.IMAGE_TO_IMAGE, actual.type)
        Assert.assertEquals("base64", actual.image)
    }

    @Test
    fun `given attempt to generate img2img, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.imageToImage(API_KEY, ENGINE_ID, any(), any())
        } throws stubException

        val actual = runCatching {
            remoteDataSource.imageToImage(
                apiKey = API_KEY,
                engineId = ENGINE_ID,
                payload = mockImageToImagePayload.copy(base64Image = IMAGE_BASE_64),
            )
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "sk-5598"
        const val ENGINE_ID = "engine_5598"
        const val IMAGE_BASE_64 = "aW1hZ2U="
    }
}
