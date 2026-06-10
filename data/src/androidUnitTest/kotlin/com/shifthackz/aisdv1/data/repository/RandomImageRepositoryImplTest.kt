package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class RandomImageRepositoryImplTest {

    private val stubBytes = byteArrayOf(1, 2, 3)
    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<RandomImageDataSource.Remote>()

    private val repository = RandomImageRepositoryImpl(stubRemoteDataSource)

    @Test
    fun `given attempt to fetch and get bytes, remote returns image, expected valid bytes value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch()
        } returns stubBytes

        val actual = repository.fetchAndGet()

        Assert.assertSame(stubBytes, actual)
    }

    @Test
    fun `given attempt to fetch and get bytes, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetch()
        } throws stubException

        val actual = runCatching { repository.fetchAndGet() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
