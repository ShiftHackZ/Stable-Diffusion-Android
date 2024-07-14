package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockStabilityAiEnginesRaw
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StabilityAiEnginesRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubApi = mockk<StabilityAiApi>()

    private val remoteDataSource = StabilityAiEnginesRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch engines, api returns success response, expected valid engines list value`() {
        every {
            stubApi.fetchEngines()
        } returns Single.just(mockStabilityAiEnginesRaw)

        remoteDataSource
            .fetch()
            .test()
            .assertNoErrors()
            .assertValue { engines ->
                engines is List<StabilityAiEngine>
                        && engines.size == mockStabilityAiEnginesRaw.size
                        && engines.any { it.id == "5598" }
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch engines, api returns empty response, expected empty engines list value`() {
        every {
            stubApi.fetchEngines()
        } returns Single.just(emptyList())

        remoteDataSource
            .fetch()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch engines, api returns error response, expected error value`() {
        every {
            stubApi.fetchEngines()
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
