package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockSupporterEntities
import com.shifthackz.aisdv1.data.mocks.mockSupporters
import com.shifthackz.aisdv1.storage.db.persistent.dao.SupporterDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SupportersLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<SupporterDao>()

    private val localDataSource = SupportersLocalDataSource(stubDao)

    @Test
    fun `given attempt to get supporters, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns mockSupporterEntities

        val actual = localDataSource.getAll()

        Assert.assertEquals(mockSupporters.size, actual.size)
    }

    @Test
    fun `given attempt to get supporters, dao returns empty list, expected empty domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns emptyList()

        val actual = localDataSource.getAll()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to get supporters, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } throws stubException

        val actual = runCatching { localDataSource.getAll() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert supporters, dao replaces list, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        localDataSource.save(mockSupporters)

        coVerify {
            stubDao.deleteAll()
            stubDao.insertList(any())
        }
    }

    @Test
    fun `given attempt to insert supporters, dao throws exception during delete, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } throws stubException

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.save(mockSupporters) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert supporters, dao throws exception during insertion, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } throws stubException

        val actual = runCatching { localDataSource.save(mockSupporters) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
