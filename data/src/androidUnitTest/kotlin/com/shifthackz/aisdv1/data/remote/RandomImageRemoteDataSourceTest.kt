package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class RandomImageRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubBytes = byteArrayOf(1, 2, 3)
    private val stubApi = mockk<ImageCdnApi>()

    private val remoteDataSource = RandomImageRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch bytes with random image, api request succeed, expected valid bytes value`() = runTest {
        coEvery {
            stubApi.fetchRandomImageBytes()
        } returns stubBytes

        val actual = remoteDataSource.fetch()

        Assert.assertSame(stubBytes, actual)
    }

    @Test
    fun `given attempt to fetch bytes with random image, api request fails, expected error value`() = runTest {
        coEvery {
            stubApi.fetchRandomImageBytes()
        } throws stubException

        val actual = runCatching { remoteDataSource.fetch() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
