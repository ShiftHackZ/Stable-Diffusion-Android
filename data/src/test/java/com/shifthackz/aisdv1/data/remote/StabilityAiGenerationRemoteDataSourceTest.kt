package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockBadStabilityGenerationResponse
import com.shifthackz.aisdv1.data.mocks.mockStabilityGenerationResponse
import com.shifthackz.aisdv1.data.mocks.mockTextToImagePayload
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiApi
import com.shifthackz.aisdv1.network.error.StabilityAiErrorMapper
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class StabilityAiGenerationRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<StabilityAiApi>()
    private val stubErrorMapper = mockk<StabilityAiErrorMapper>()

    private val remoteDataSource = StabilityAiGenerationRemoteDataSource(
        api = stubApi,
        stabilityAiErrorMapper = stubErrorMapper,
    )

    @Before
    fun initialize() {
        every {
            stubErrorMapper.invoke<Any>(any())
        } returns Single.error(stubException)
    }

    @Test
    fun `given attempt to validate bearer token, api returns success response, expected true`() {
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
    fun `given attempt to validate bearer token, api returns error response, expected false`() {
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
    fun `given attempt to generate txt2img, api returns result, expected valid ai generation result value`() {
        every {
            stubApi.textToImage(any(), any())
        } returns Single.just(mockStabilityGenerationResponse)

        remoteDataSource
            .textToImage("5598", mockTextToImagePayload)
            .test()
            .assertNoErrors()
            .assertValue { it is AiGenerationResult }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to generate txt2img, api returns empty result, expected error value`() {
        every {
            stubApi.textToImage(any(), any())
        } returns Single.just(mockBadStabilityGenerationResponse)

        remoteDataSource
            .textToImage("5598", mockTextToImagePayload)
            .test()
            .assertError { true }
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to generate txt2img, api returns error response, expected error value`() {
        every {
            stubApi.textToImage(any(), any())
        } returns Single.error(stubException)

        remoteDataSource
            .textToImage("5598", mockTextToImagePayload)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
