package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mocks.mockSupporters
import com.shifthackz.aisdv1.data.mocks.mockSupportersRaw
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SupportersRemoteDataSourceTest {

    private val stubException = Throwable("Bad response.")
    private val stubApi = mockk<SdaiAppApi>()

    private val remoteDataSource = KtorSupportersRemoteDataSource(stubApi)

    @Test
    fun `given api returns supporters, expected valid domain models list value`() = runTest {
        coEvery {
            stubApi.fetchSupporters()
        } returns mockSupportersRaw

        val actual = remoteDataSource.fetch()

        Assert.assertEquals(mockSupporters.size, actual.size)
    }

    @Test
    fun `given api returns empty list, expected empty domain models list value`() = runTest {
        coEvery {
            stubApi.fetchSupporters()
        } returns emptyList()

        val actual = remoteDataSource.fetch()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given api returns error, expected error value`() = runTest {
        coEvery {
            stubApi.fetchSupporters()
        } throws stubException

        val actual = runCatching { remoteDataSource.fetch() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
