package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStabilityAiEngines
import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class RemoteStabilityAiEnginesRepositoryTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<StabilityAiEnginesRemoteDataSource>()

    private val repository = RemoteStabilityAiEnginesRepository(stubRemoteDataSource)

    @Test
    fun `given attempt to fetch engines, remote returns data, expected valid domain model list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } returns mockStabilityAiEngines

        val actual = repository.fetch(API_KEY)

        Assert.assertEquals(mockStabilityAiEngines, actual)
    }

    @Test
    fun `given attempt to fetch engines, remote returns empty data, expected empty domain model list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } returns emptyList()

        val actual = repository.fetch(API_KEY)

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to fetch engines, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch(API_KEY)
        } throws stubException

        val actual = runCatching { repository.fetch(API_KEY) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val API_KEY = "api_key"
    }
}
