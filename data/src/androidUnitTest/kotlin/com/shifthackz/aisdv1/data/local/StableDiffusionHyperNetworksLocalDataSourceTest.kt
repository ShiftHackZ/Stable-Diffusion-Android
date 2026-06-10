package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionHyperNetworkEntities
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionHyperNetworks
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionHyperNetworkDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class StableDiffusionHyperNetworksLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<StableDiffusionHyperNetworkDao>()

    private val localDataSource = StableDiffusionHyperNetworksLocalDataSource(stubDao)

    @Test
    fun `given attempt to get hypernetworks, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns mockStableDiffusionHyperNetworkEntities

        val actual = localDataSource.getHyperNetworks()

        Assert.assertEquals(mockStableDiffusionHyperNetworks, actual)
    }

    @Test
    fun `given attempt to get hypernetworks, dao returns empty list, expected empty domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns emptyList()

        val actual = localDataSource.getHyperNetworks()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to get hypernetworks, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } throws stubException

        val actual = runCatching { localDataSource.getHyperNetworks() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert hypernetworks, dao replaces list, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        localDataSource.insertHyperNetworks(mockStableDiffusionHyperNetworks)

        coVerify {
            stubDao.deleteAll()
            stubDao.insertList(any())
        }
    }

    @Test
    fun `given attempt to insert hypernetworks, dao throws exception during delete, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } throws stubException

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.insertHyperNetworks(mockStableDiffusionHyperNetworks) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert hypernetworks, dao throws exception during insertion, expected error value`() =
        runTest {
            coEvery {
                stubDao.deleteAll()
            } returns Unit

            coEvery {
                stubDao.insertList(any())
            } throws stubException

            val actual = runCatching { localDataSource.insertHyperNetworks(mockStableDiffusionHyperNetworks) }

            Assert.assertEquals(stubException, actual.exceptionOrNull())
        }
}
