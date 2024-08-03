package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStabilityAiEngines
import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StabilityAiEnginesRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<StabilityAiEnginesDataSource.Remote>()

    private val repository = StabilityAiEnginesRepositoryImpl(stubRemoteDataSource)

    @Test
    fun `given attempt to fetch and get engines, remote returns data, expected valid domain model list value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(mockStabilityAiEngines)

        repository
            .fetchAndGet()
            .test()
            .assertNoErrors()
            .assertValue(mockStabilityAiEngines)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get engines, remote returns empty data, expected empty domain model list value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(emptyList())

        repository
            .fetchAndGet()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get engines, remote throws exception, expected error value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.error(stubException)

        repository
            .fetchAndGet()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
