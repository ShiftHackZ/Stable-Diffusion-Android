package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiApi
import com.shifthackz.aisdv1.network.response.StabilityCreditsResponse
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StabilityAiCreditsRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<StabilityAiApi>()

    private val remoteDataSource = StabilityAiCreditsRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch credits, api returns response with normal value, expected valid credits value`() {
        every {
            stubApi.fetchCredits()
        } returns Single.just(StabilityCreditsResponse(5598f))

        remoteDataSource
            .fetch()
            .test()
            .assertNoErrors()
            .assertValue(5598f)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch credits, api returns response with zero value, expected zero credits value`() {
        every {
            stubApi.fetchCredits()
        } returns Single.just(StabilityCreditsResponse(0f))

        remoteDataSource
            .fetch()
            .test()
            .assertNoErrors()
            .assertValue(0f)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch credits, api returns response with null value, expected zero credits value`() {
        every {
            stubApi.fetchCredits()
        } returns Single.just(StabilityCreditsResponse(null))

        remoteDataSource
            .fetch()
            .test()
            .assertNoErrors()
            .assertValue(0f)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch credits, api returns error response, expected error value`() {
        every {
            stubApi.fetchCredits()
        } returns Single.error(stubException)

        remoteDataSource
            .fetch()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
