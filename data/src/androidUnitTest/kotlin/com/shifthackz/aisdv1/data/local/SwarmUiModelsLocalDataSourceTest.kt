package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockSwarmUiModelEntities
import com.shifthackz.aisdv1.data.mocks.mockSwarmUiModels
import com.shifthackz.aisdv1.storage.db.cache.dao.SwarmUiModelDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SwarmUiModelsLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<SwarmUiModelDao>()

    private val localDataSource = SwarmUiModelsLocalDataSource(stubDao)

    @Test
    fun `given attempt to get models, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns mockSwarmUiModelEntities

        val actual = localDataSource.getModels()

        Assert.assertEquals(mockSwarmUiModels.size, actual.size)
    }

    @Test
    fun `given attempt to get models, dao returns empty list, expected empty domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns emptyList()

        val actual = localDataSource.getModels()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to get models, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } throws stubException

        val actual = runCatching { localDataSource.getModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert models, dao replaces list, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        localDataSource.insertModels(mockSwarmUiModels)

        coVerify {
            stubDao.deleteAll()
            stubDao.insertList(any())
        }
    }

    @Test
    fun `given attempt to insert models, dao throws exception during delete, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } throws stubException

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.insertModels(mockSwarmUiModels) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert models, dao throws exception during insertion, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } throws stubException

        val actual = runCatching { localDataSource.insertModels(mockSwarmUiModels) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
