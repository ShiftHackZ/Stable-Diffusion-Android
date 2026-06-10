package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockStabilityAiEnginesRaw
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiEnginesApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KtorStabilityAiEnginesRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<StabilityAiEnginesApi>()

    private val remoteDataSource = KtorStabilityAiEnginesRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch engines, api returns success response, expected valid engines list value`() = runTest {
        coEvery {
            stubApi.fetchEngines(API_KEY)
        } returns mockStabilityAiEnginesRaw

        val actual: List<StabilityAiEngine> = remoteDataSource.fetch(API_KEY)

        Assert.assertEquals(mockStabilityAiEnginesRaw.size, actual.size)
        Assert.assertTrue(actual.any { it.id == "5598" })
    }

    @Test
    fun `given attempt to fetch engines, api returns empty response, expected empty engines list value`() = runTest {
        coEvery {
            stubApi.fetchEngines(API_KEY)
        } returns emptyList()

        val actual = remoteDataSource.fetch(API_KEY)

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to fetch engines, api returns error response, expected error value`() = runTest {
        coEvery {
            stubApi.fetchEngines(API_KEY)
        } throws stubException

        val actual = runCatching { remoteDataSource.fetch(API_KEY) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "api_key"
    }
}
