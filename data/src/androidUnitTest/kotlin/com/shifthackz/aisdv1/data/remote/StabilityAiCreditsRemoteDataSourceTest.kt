package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiGenerationApi
import com.shifthackz.aisdv1.network.response.StabilityCreditsResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class StabilityAiCreditsRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<StabilityAiGenerationApi>()

    private val remoteDataSource = KtorStabilityAiCreditsRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch credits, api returns response with normal value, expected valid credits value`() = runTest {
        coEvery {
            stubApi.fetchCredits(API_KEY)
        } returns StabilityCreditsResponse(5598f)

        val actual = remoteDataSource.fetch(API_KEY)

        Assert.assertEquals(5598f, actual)
    }

    @Test
    fun `given attempt to fetch credits, api returns response with zero value, expected zero credits value`() = runTest {
        coEvery {
            stubApi.fetchCredits(API_KEY)
        } returns StabilityCreditsResponse(0f)

        val actual = remoteDataSource.fetch(API_KEY)

        Assert.assertEquals(0f, actual)
    }

    @Test
    fun `given attempt to fetch credits, api returns response with null value, expected zero credits value`() = runTest {
        coEvery {
            stubApi.fetchCredits(API_KEY)
        } returns StabilityCreditsResponse(null)

        val actual = remoteDataSource.fetch(API_KEY)

        Assert.assertEquals(0f, actual)
    }

    @Test
    fun `given attempt to fetch credits, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchCredits(API_KEY)
        } throws stubException

        val actual = runCatching {
            remoteDataSource.fetch(API_KEY)
        }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "sk-5598"
    }
}
