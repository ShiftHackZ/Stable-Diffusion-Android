package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.data.mocks.mockHuggingFaceModelEntities
import com.shifthackz.aisdv1.data.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.storage.db.persistent.dao.HuggingFaceModelDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class HuggingFaceModelsLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<HuggingFaceModelDao>()

    private val localDataSource = HuggingFaceModelsLocalDataSource(stubDao)

    @Test
    fun `given attempt to get all, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.query()
        } returns mockHuggingFaceModelEntities

        val actual = localDataSource.getAll()

        Assert.assertEquals(mockHuggingFaceModelEntities.mapEntityToDomain(), actual)
    }

    @Test
    fun `given attempt to get all, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.query()
        } throws stubException

        val actual = runCatching { localDataSource.getAll() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert list, dao insert success, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        localDataSource.save(mockHuggingFaceModels)

        coVerify {
            stubDao.deleteAll()
            stubDao.insertList(any())
        }
    }

    @Test
    fun `given attempt to insert list, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } throws stubException

        val actual = runCatching { localDataSource.save(mockHuggingFaceModels) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
