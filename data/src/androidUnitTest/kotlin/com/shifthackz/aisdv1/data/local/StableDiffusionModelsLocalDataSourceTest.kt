package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionModelEntities
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionModelDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StableDiffusionModelsLocalDataSourceTest {

    private val stubException = RuntimeException("Database error.")
    private val stubDao = mockk<StableDiffusionModelDao>()

    private val localDataSource = StableDiffusionModelsLocalDataSource(stubDao)

    @Test
    fun `given attempt to get models, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns mockStableDiffusionModelEntities

        assertEquals(mockStableDiffusionModels, localDataSource.getModels())
    }

    @Test
    fun `given attempt to get models, dao returns empty list, expected empty domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns emptyList()

        assertEquals(emptyList<Any>(), localDataSource.getModels())
    }

    @Test
    fun `given attempt to get models, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } throws stubException

        val actual = runCatching { localDataSource.getModels() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to insert models, dao replaces list, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.insertModels(mockStableDiffusionModels) }

        assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to insert models, dao throws exception during delete, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } throws stubException

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.insertModels(mockStableDiffusionModels) }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to insert models, dao throws exception during insertion, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } throws stubException

        val actual = runCatching { localDataSource.insertModels(mockStableDiffusionModels) }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
