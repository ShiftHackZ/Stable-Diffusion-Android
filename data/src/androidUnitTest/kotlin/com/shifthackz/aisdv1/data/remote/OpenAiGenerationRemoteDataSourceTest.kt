package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockBadOpenAiResponse
import com.shifthackz.aisdv1.data.mocks.mockSuccessOpenAiResponse
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.network.api.openai.OpenAiGenerationApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KtorOpenAiGenerationRemoteDataSourceTest {

    private val stubApiException = Throwable("Internal server error.")
    private val stubApi = mockk<OpenAiGenerationApi>()

    private val remoteDataSource = KtorOpenAiGenerationRemoteDataSource(stubApi)

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
        } throws Throwable("Bad api key.")

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertFalse(actual)
    }

    @Test
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() =
        runTest {
            coEvery {
                stubApi.generateImage(API_KEY, any())
            } returns mockSuccessOpenAiResponse

            val actual = remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)

            Assert.assertEquals(AiGenerationResult.Type.TEXT_TO_IMAGE, actual.type)
            Assert.assertEquals("base64", actual.image)
        }

    @Test
    fun `given attempt to generate txt2img, api returns empty result, expected error value`() = runTest {
        coEvery {
            stubApi.generateImage(API_KEY, any())
        } returns mockBadOpenAiResponse

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        }

        Assert.assertTrue(actual.exceptionOrNull() is IllegalStateException)
        Assert.assertEquals("Got null data object from API.", actual.exceptionOrNull()?.message)
    }

    @Test
    fun `given attempt to generate txt2img, api request fails, expected error value`() = runTest {
        coEvery {
            stubApi.generateImage(API_KEY, any())
        } throws stubApiException

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        }

        Assert.assertEquals(stubApiException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "openai-api-key"
    }
}
