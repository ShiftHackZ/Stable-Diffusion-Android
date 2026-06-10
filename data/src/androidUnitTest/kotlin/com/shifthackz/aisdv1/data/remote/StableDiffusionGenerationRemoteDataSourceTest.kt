package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToStableDiffusionRequest
import com.shifthackz.aisdv1.data.mocks.mockImageToImagePayload
import com.shifthackz.aisdv1.data.mocks.mockSdGenerationResponse
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111GenerationApi
import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class StableDiffusionGenerationRemoteDataSourceTest {

    private val stubException = RuntimeException("Internal server error.")
    private val stubApi = mockk<Automatic1111GenerationApi>()

    private val remoteDataSource = KtorStableDiffusionGenerationRemoteDataSource(
        api = stubApi,
    )

    @Test
    fun `given attempt to do health check, api returns success response, expected complete value`() = runTest {
        coEvery {
            stubApi.healthCheck(BASE_URL, AUTHORIZATION)
        } returns Unit

        val actual = runCatching {
            remoteDataSource.checkAvailability(BASE_URL, AUTHORIZATION_CREDENTIALS)
        }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to do health check, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.healthCheck(BASE_URL, AUTHORIZATION)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.checkAvailability(BASE_URL, AUTHORIZATION_CREDENTIALS)
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() = runTest {
        coEvery {
            stubApi.textToImage(BASE_URL, AUTHORIZATION, mockTextToImagePayload.mapToStableDiffusionRequest())
        } returns mockSdGenerationResponse

        val actual = remoteDataSource.textToImage(
            BASE_URL,
            AUTHORIZATION_CREDENTIALS,
            mockTextToImagePayload,
        )

        Assert.assertEquals(1, actual.size)
        Assert.assertEquals("base64", actual.first().image)
    }

    @Test
    fun `given attempt to generate txt2img, api returns error, expected error value`() = runTest {
        coEvery {
            stubApi.textToImage(BASE_URL, AUTHORIZATION, mockTextToImagePayload.mapToStableDiffusionRequest())
        } throws stubException

        val actual = runCatching {
            remoteDataSource.textToImage(
                BASE_URL,
                AUTHORIZATION_CREDENTIALS,
                mockTextToImagePayload,
            )
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to generate img2img, api returns result, expected valid ai generation result value`() = runTest {
        coEvery {
            stubApi.imageToImage(BASE_URL, AUTHORIZATION, mockImageToImagePayload.mapToStableDiffusionRequest())
        } returns mockSdGenerationResponse

        val actual = remoteDataSource.imageToImage(
            BASE_URL,
            AUTHORIZATION_CREDENTIALS,
            mockImageToImagePayload,
        )

        Assert.assertEquals(1, actual.size)
        Assert.assertEquals("base64", actual.first().image)
    }

    @Test
    fun `given attempt to generate img2img, api returns error, expected error value`() = runTest {
        coEvery {
            stubApi.imageToImage(BASE_URL, AUTHORIZATION, mockImageToImagePayload.mapToStableDiffusionRequest())
        } throws stubException

        val actual = runCatching {
            remoteDataSource.imageToImage(
                BASE_URL,
                AUTHORIZATION_CREDENTIALS,
                mockImageToImagePayload,
            )
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to interrupt generation, api returns success response, expected complete value`() = runTest {
        coEvery {
            stubApi.interrupt(BASE_URL, AUTHORIZATION)
        } returns Unit

        val actual = runCatching {
            remoteDataSource.interruptGeneration(BASE_URL, AUTHORIZATION_CREDENTIALS)
        }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to interrupt generation, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.interrupt(BASE_URL, AUTHORIZATION)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.interruptGeneration(BASE_URL, AUTHORIZATION_CREDENTIALS)
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val BASE_URL = "http://192.168.0.1:7860"
        val AUTHORIZATION_CREDENTIALS = AuthorizationCredentials.HttpBasic(
            login = "5598",
            password = "151297",
        )
        val AUTHORIZATION = BasicHttpAuthorization(
            login = "5598",
            password = "151297",
        )
    }
}
