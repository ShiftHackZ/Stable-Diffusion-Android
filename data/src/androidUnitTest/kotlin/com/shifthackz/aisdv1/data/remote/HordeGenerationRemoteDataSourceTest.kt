package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.encodeBase64NoWrap
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.network.api.horde.HordeGenerationApi
import com.shifthackz.aisdv1.network.response.HordeGenerationAsyncResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckFullResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckResponse
import com.shifthackz.aisdv1.network.response.HordeUserResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HordeGenerationRemoteDataSourceTest {

    private val stubBytes = byteArrayOf(5, 5, 9, 8)
    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<HordeGenerationApi>()
    private val stubHordeStatusSource = mockk<HordeGenerationDataSource.StatusSource>()

    private val remoteDataSource = KtorHordeGenerationRemoteDataSource(
        api = stubApi,
        statusSource = stubHordeStatusSource,
    )

    @Before
    fun initialize() {
        every {
            stubHordeStatusSource.id = any()
        } returns Unit

        justRun {
            stubHordeStatusSource.update(any())
        }
    }

    @Test
    fun `given attempt to validate api key, api returns user with valid id, expected true value`() = runTest {
        coEvery {
            stubApi.checkHordeApiKey(API_KEY)
        } returns HordeUserResponse(5598)

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertTrue(actual)
    }

    @Test
    fun `given attempt to validate api key, api returns null, expected false value`() = runTest {
        coEvery {
            stubApi.checkHordeApiKey(API_KEY)
        } returns HordeUserResponse(null)

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertFalse(actual)
    }

    @Test
    fun `given attempt to validate api key, api throws exception, expected false value`() = runTest {
        coEvery {
            stubApi.checkHordeApiKey(API_KEY)
        } throws stubException

        val actual = remoteDataSource.validateApiKey(API_KEY)

        Assert.assertFalse(actual)
    }

    @Test
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() = runTest {
        coEvery {
            stubApi.generateAsync(API_KEY, any())
        } returns HordeGenerationAsyncResponse(id = REQUEST_ID)

        coEvery {
            stubApi.checkGeneration(API_KEY, REQUEST_ID)
        } returnsMany listOf(
            HordeGenerationCheckResponse(done = false, waitTime = 5, queuePosition = 2),
            HordeGenerationCheckResponse(done = true, isPossible = true),
        )

        coEvery {
            stubApi.checkStatus(API_KEY, REQUEST_ID)
        } returns HordeGenerationCheckFullResponse(
            generations = listOf(HordeGenerationCheckFullResponse.Generation(img = IMAGE_URL)),
        )

        coEvery {
            stubApi.downloadImage(IMAGE_URL)
        } returns stubBytes

        val actual = remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)

        Assert.assertEquals(AiGenerationResult.Type.TEXT_TO_IMAGE, actual.type)
        Assert.assertEquals(stubBytes.encodeBase64NoWrap(), actual.image)
        verify {
            stubHordeStatusSource.id = REQUEST_ID
            stubHordeStatusSource.update(HordeProcessStatus(waitTimeSeconds = 5, queuePosition = 2))
        }
    }

    @Test
    fun `given attempt to generate img2img, api returns result, expected valid ai generation result value`() = runTest {
        coEvery {
            stubApi.generateAsync(API_KEY, any())
        } returns HordeGenerationAsyncResponse(id = REQUEST_ID)

        coEvery {
            stubApi.checkGeneration(API_KEY, REQUEST_ID)
        } returns HordeGenerationCheckResponse(done = true, isPossible = true)

        coEvery {
            stubApi.checkStatus(API_KEY, REQUEST_ID)
        } returns HordeGenerationCheckFullResponse(
            generations = listOf(HordeGenerationCheckFullResponse.Generation(img = IMAGE_URL)),
        )

        coEvery {
            stubApi.downloadImage(IMAGE_URL)
        } returns stubBytes

        val actual = remoteDataSource.imageToImage(API_KEY, mockImageToImagePayload)

        Assert.assertEquals(AiGenerationResult.Type.IMAGE_TO_IMAGE, actual.type)
        Assert.assertEquals(stubBytes.encodeBase64NoWrap(), actual.image)
    }

    @Test
    fun `given async response with decimal kudos, expected decoded response`() {
        val actual = Json.decodeFromString<HordeGenerationAsyncResponse>(
            """{"id":"$REQUEST_ID","kudos":0.0}""",
        )

        Assert.assertEquals(REQUEST_ID, actual.id)
        Assert.assertEquals(0.0, actual.kudos ?: -1.0, 0.0)
    }

    @Test
    fun `given attempt to generate, async response id is null, expected error value`() = runTest {
        coEvery {
            stubApi.generateAsync(API_KEY, any())
        } returns HordeGenerationAsyncResponse(id = null)

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        }

        Assert.assertEquals("Horde returned null generation id", actual.exceptionOrNull()?.message)
    }

    @Test
    fun `given attempt to generate, response is impossible, expected error value`() = runTest {
        coEvery {
            stubApi.generateAsync(API_KEY, any())
        } returns HordeGenerationAsyncResponse(id = REQUEST_ID)

        coEvery {
            stubApi.checkGeneration(API_KEY, REQUEST_ID)
        } returns HordeGenerationCheckResponse(done = false, isPossible = false)

        val actual = runCatching {
            remoteDataSource.textToImage(API_KEY, mockTextToImagePayload)
        }

        Assert.assertEquals("Response is not possible", actual.exceptionOrNull()?.message)
    }

    @Test
    fun `given attempt to interrupt generation, id present in cache, api returns success, expected complete value`() = runTest {
        every {
            stubHordeStatusSource.id
        } returns REQUEST_ID

        coEvery {
            stubApi.cancelRequest(API_KEY, REQUEST_ID)
        } returns Unit

        val actual = runCatching {
            remoteDataSource.interruptGeneration(API_KEY)
        }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to interrupt generation, id present in cache, api throws exception, expected error value`() = runTest {
        every {
            stubHordeStatusSource.id
        } returns REQUEST_ID

        coEvery {
            stubApi.cancelRequest(API_KEY, REQUEST_ID)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.interruptGeneration(API_KEY)
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to interrupt generation, no id present in cache, expected error value`() = runTest {
        every {
            stubHordeStatusSource.id
        } returns null

        val actual = runCatching {
            remoteDataSource.interruptGeneration(API_KEY)
        }

        Assert.assertTrue(actual.exceptionOrNull() is IllegalStateException)
        Assert.assertEquals("No cached request id", actual.exceptionOrNull()?.message)
    }

    private companion object {
        const val API_KEY = "sk-5598"
        const val REQUEST_ID = "request_5598"
        const val IMAGE_URL = "https://stablehorde.net/image.png"
    }
}
