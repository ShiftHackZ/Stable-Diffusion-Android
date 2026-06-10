package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionLoraEntities
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionLoras
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionLoraDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LorasLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<StableDiffusionLoraDao>()

    private val localDataSource = LorasLocalDataSource(stubDao)

    @Test
    fun `given attempt to get loras, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns mockStableDiffusionLoraEntities

        val actual = localDataSource.getLoras()

        Assert.assertEquals(mockStableDiffusionLoras, actual)
    }

    @Test
    fun `given attempt to get loras, dao returns empty list, expected empty domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns emptyList()

        val actual = localDataSource.getLoras()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to get loras, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } throws stubException

        val actual = runCatching { localDataSource.getLoras() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert loras, dao replaces list, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        localDataSource.insertLoras(mockStableDiffusionLoras)

        coVerify {
            stubDao.deleteAll()
            stubDao.insertList(any())
        }
    }

    @Test
    fun `given attempt to insert loras, dao throws exception during delete, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } throws stubException

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.insertLoras(mockStableDiffusionLoras) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert loras, dao throws exception during insertion, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } throws stubException

        val actual = runCatching { localDataSource.insertLoras(mockStableDiffusionLoras) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
