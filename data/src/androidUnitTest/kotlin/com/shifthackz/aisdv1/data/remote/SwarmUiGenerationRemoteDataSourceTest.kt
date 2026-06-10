package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockKtorSwarmUiGenerationResponse
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult.Type
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiGenerationApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KtorSwarmUiGenerationRemoteDataSourceTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubApi = mockk<SwarmUiGenerationApi>()

    private val remoteDataSource = KtorSwarmUiGenerationRemoteDataSource(stubApi)

    @Test
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() =
        runTest {
            coEvery {
                stubApi.generate(BASE_URL, any(), null)
            } returns mockKtorSwarmUiGenerationResponse

            coEvery {
                stubApi.downloadImage("$BASE_URL/tmp/img.jpg", null)
            } returns IMAGE_BYTES

            val actual = remoteDataSource.textToImage(
                baseUrl = BASE_URL,
                sessionId = SESSION_ID,
                model = MODEL,
                credentials = AuthorizationCredentials.None,
                payload = mockTextToImagePayload,
            )

            Assert.assertEquals(Type.TEXT_TO_IMAGE, actual.type)
            Assert.assertEquals(EXPECTED_BASE64, actual.image)
        }

    @Test
    fun `given attempt to generate txt2img, api returns error, expected error value`() = runTest {
        coEvery {
            stubApi.generate(BASE_URL, any(), null)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.textToImage(
                baseUrl = BASE_URL,
                sessionId = SESSION_ID,
                model = MODEL,
                credentials = AuthorizationCredentials.None,
                payload = mockTextToImagePayload,
            )
        }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate img2img, api returns result, expected valid ai generation result value`() =
        runTest {
            coEvery {
                stubApi.generate(BASE_URL, any(), null)
            } returns mockKtorSwarmUiGenerationResponse

            coEvery {
                stubApi.downloadImage("$BASE_URL/tmp/img.jpg", null)
            } returns IMAGE_BYTES

            val actual = remoteDataSource.imageToImage(
                baseUrl = BASE_URL,
                sessionId = SESSION_ID,
                model = MODEL,
                credentials = AuthorizationCredentials.None,
                payload = mockImageToImagePayload,
            )

            Assert.assertEquals(Type.IMAGE_TO_IMAGE, actual.type)
            Assert.assertEquals(EXPECTED_BASE64, actual.image)
        }

    @Test
    fun `given attempt to generate img2img, api returns error, expected error value`() = runTest {
        coEvery {
            stubApi.generate(BASE_URL, any(), null)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.imageToImage(
                baseUrl = BASE_URL,
                sessionId = SESSION_ID,
                model = MODEL,
                credentials = AuthorizationCredentials.None,
                payload = mockImageToImagePayload,
            )
        }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val BASE_URL = "http://192.168.0.1:7801"
        const val SESSION_ID = "5598"
        const val MODEL = "OpenStableDiffusion"
        val IMAGE_BYTES = "image".encodeToByteArray()
        const val EXPECTED_BASE64 = "aW1hZ2U="
    }
}
