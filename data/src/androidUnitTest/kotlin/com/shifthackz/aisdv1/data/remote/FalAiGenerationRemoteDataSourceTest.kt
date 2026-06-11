package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.network.api.falai.FalAiGenerationApi
import com.shifthackz.aisdv1.network.response.FalAiGenerationResponse
import com.shifthackz.aisdv1.network.response.FalAiImage
import com.shifthackz.aisdv1.network.response.FalAiQueueStatusResponse
import com.shifthackz.aisdv1.network.response.FalAiQueueSubmitResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KtorFalAiGenerationRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<FalAiGenerationApi>()

    private val remoteDataSource = KtorFalAiGenerationRemoteDataSource(
        api = stubApi,
        pollIntervalMillis = 0L,
        maxPollAttempts = 3,
    )

    @Test
    fun `given attempt to validate api key, api returns success response, expected true`() = runTest {
        coEvery {
            stubApi.validateApiKey(API_KEY)
        } returns Unit

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertTrue(actual)
    }

    @Test
    fun `given attempt to validate api key, api returns error response, expected false`() = runTest {
        coEvery {
            stubApi.validateApiKey(API_KEY)
        } throws Throwable("Invalid api key.")

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertFalse(actual)
    }

    @Test
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() =
        runTest {
            coEvery {
                stubApi.submitTextToImage(API_KEY, any(), any())
            } returns FalAiQueueSubmitResponse(
                responseUrl = RESPONSE_URL,
                statusUrl = STATUS_URL,
            )

            coEvery {
                stubApi.getQueueStatus(API_KEY, STATUS_URL)
            } returnsMany listOf(
                FalAiQueueStatusResponse(status = "IN_PROGRESS"),
                FalAiQueueStatusResponse(status = "COMPLETED", responseUrl = RESPONSE_URL),
            )

            coEvery {
                stubApi.getQueueResult(API_KEY, RESPONSE_URL)
            } returns FalAiGenerationResponse(
                images = listOf(FalAiImage(url = IMAGE_URL)),
                seed = 5598L,
            )

            coEvery {
                stubApi.downloadImage(IMAGE_URL)
            } returns byteArrayOf(1, 2, 3)

            val actual = remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)

            Assert.assertEquals(AiGenerationResult.Type.TEXT_TO_IMAGE, actual.first().type)
            Assert.assertEquals("AQID", actual.first().image)
            Assert.assertEquals("5598", actual.first().seed)
        }

    @Test
    fun `given attempt to generate img2img, api returns result, expected valid ai generation result value`() =
        runTest {
            coEvery {
                stubApi.submitImageToImage(API_KEY, any(), any())
            } returns FalAiQueueSubmitResponse(
                responseUrl = RESPONSE_URL,
                statusUrl = STATUS_URL,
            )

            coEvery {
                stubApi.getQueueStatus(API_KEY, STATUS_URL)
            } returns FalAiQueueStatusResponse(status = "COMPLETED", responseUrl = RESPONSE_URL)

            coEvery {
                stubApi.getQueueResult(API_KEY, RESPONSE_URL)
            } returns FalAiGenerationResponse(
                images = listOf(FalAiImage(url = "data:image/png;base64,AQID")),
                seed = 5598L,
            )

            val actual = remoteDataSource.imageToImage(API_KEY, mockImageToImagePayload)

            Assert.assertEquals(AiGenerationResult.Type.IMAGE_TO_IMAGE, actual.first().type)
            Assert.assertEquals("AQID", actual.first().image)
            Assert.assertEquals("5598", actual.first().seed)
        }

    @Test
    fun `given attempt to generate txt2img, api returns empty result, expected error value`() = runTest {
        coEvery {
            stubApi.submitTextToImage(API_KEY, any(), any())
        } returns FalAiQueueSubmitResponse(
            responseUrl = RESPONSE_URL,
            statusUrl = STATUS_URL,
        )

        coEvery {
            stubApi.getQueueStatus(API_KEY, STATUS_URL)
        } returns FalAiQueueStatusResponse(status = "COMPLETED", responseUrl = RESPONSE_URL)

        coEvery {
            stubApi.getQueueResult(API_KEY, RESPONSE_URL)
        } returns FalAiGenerationResponse(images = emptyList())

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        }

        Assert.assertTrue(actual.exceptionOrNull() is IllegalStateException)
        Assert.assertEquals(
            "Fal.ai did not return generated image URL.",
            actual.exceptionOrNull()?.message,
        )
    }

    @Test
    fun `given attempt to generate txt2img, api request fails, expected error value`() = runTest {
        coEvery {
            stubApi.submitTextToImage(API_KEY, any(), any())
        } throws stubException

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "fal-ai-api-key"
        const val STATUS_URL = "https://queue.fal.run/status"
        const val RESPONSE_URL = "https://queue.fal.run/result"
        const val IMAGE_URL = "https://fal.media/image.png"
    }
}
