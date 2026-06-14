package com.shifthackz.aisdv1.network.api.arliai

import com.shifthackz.aisdv1.network.client.NetworkUsageCategory
import com.shifthackz.aisdv1.network.client.NetworkUsageCounter
import com.shifthackz.aisdv1.network.client.defaultNetworkJson
import com.shifthackz.aisdv1.network.request.ArliAiImageToImageRequest
import com.shifthackz.aisdv1.network.request.ArliAiTextToImageRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class KtorArliAiGenerationApiTest {

    private val records = mutableListOf<Pair<NetworkUsageCategory, Long>>()

    @After
    fun tearDown() {
        NetworkUsageCounter.recorder = null
    }

    @Test
    fun `given models request, expected config traffic recorded`() = runNetworkUsageTest {
        val response = """[{"title":"Dream","model_name":"dream"}]"""
        val api = createApi(response)

        val models = api.fetchModels(API_KEY)

        assertEquals("Dream", models.first().title)
        assertEquals(
            listOf(NetworkUsageCategory.CONFIGS to response.byteSize),
            records,
        )
    }

    @Test
    fun `given text to image request, expected inference traffic recorded`() = runNetworkUsageTest {
        val request = textToImageRequest()
        val response = """{"images":["AQID"],"info":"{}"}"""
        val api = createApi(response)

        val result = api.textToImage(API_KEY, request)

        assertEquals(listOf("AQID"), result.images)
        assertEquals(
            listOf(
                NetworkUsageCategory.INFERENCE to defaultNetworkJson.encodeToString(request).byteSize,
                NetworkUsageCategory.INFERENCE to response.byteSize,
            ),
            records,
        )
    }

    @Test
    fun `given image to image request, expected inference traffic recorded`() = runNetworkUsageTest {
        val request = imageToImageRequest()
        val response = """{"images":["BAUG"],"info":"{}"}"""
        val api = createApi(response)

        val result = api.imageToImage(API_KEY, request)

        assertEquals(listOf("BAUG"), result.images)
        assertEquals(
            listOf(
                NetworkUsageCategory.INFERENCE to defaultNetworkJson.encodeToString(request).byteSize,
                NetworkUsageCategory.INFERENCE to response.byteSize,
            ),
            records,
        )
    }

    private fun runNetworkUsageTest(block: suspend () -> Unit) = runBlocking {
        NetworkUsageCounter.recorder = { category, bytes ->
            records += category to bytes
        }
        block()
    }

    private fun createApi(response: String): KtorArliAiGenerationApi {
        val engine = MockEngine {
            respond(
                content = response,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        return KtorArliAiGenerationApi(
            httpClient = HttpClient(engine),
            baseUrl = BASE_URL,
        )
    }

    private val String.byteSize: Long
        get() = encodeToByteArray().size.toLong()

    private fun textToImageRequest() = ArliAiTextToImageRequest(
        sdModelCheckpoint = MODEL,
        prompt = "a cozy treehouse",
        negativePrompt = "low quality",
        steps = 20,
        samplerName = "Euler",
        width = 512,
        height = 512,
        seed = 5598L,
        cfgScale = 7.5f,
        batchSize = 1,
        restoreFaces = false,
    )

    private fun imageToImageRequest() = ArliAiImageToImageRequest(
        sdModelCheckpoint = MODEL,
        prompt = "a cozy treehouse",
        negativePrompt = "low quality",
        initImages = listOf("AQID"),
        mask = null,
        denoisingStrength = 0.65f,
        steps = 20,
        samplerName = "Euler",
        width = 512,
        height = 512,
        seed = 5598L,
        cfgScale = 7.5f,
        batchSize = 1,
        restoreFaces = false,
        maskBlur = null,
        inPaintingFill = null,
        inPaintFullRes = null,
        inPaintFullResPadding = null,
        inPaintingMaskInvert = null,
    )

    private companion object {
        const val API_KEY = "key"
        const val BASE_URL = "https://api.arliai.example"
        const val MODEL = "Illustrious-XL-v2.0"
    }
}
