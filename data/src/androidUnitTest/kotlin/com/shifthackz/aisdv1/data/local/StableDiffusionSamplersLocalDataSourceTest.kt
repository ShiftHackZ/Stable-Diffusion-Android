package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionSamplerEntities
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionSamplers
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionSamplerDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StableDiffusionSamplersLocalDataSourceTest {

    private val stubException = RuntimeException("Database error.")
    private val stubDao = mockk<StableDiffusionSamplerDao>()

    private val localDataSource = StableDiffusionSamplersLocalDataSource(stubDao)

    @Test
    fun `given attempt to get samplers, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns mockStableDiffusionSamplerEntities

        assertEquals(mockStableDiffusionSamplers, localDataSource.getSamplers())
    }

    @Test
    fun `given attempt to get samplers, dao returns empty list, expected empty domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns emptyList()

        assertEquals(emptyList<Any>(), localDataSource.getSamplers())
    }

    @Test
    fun `given attempt to get samplers, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } throws stubException

        val actual = runCatching { localDataSource.getSamplers() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to insert samplers, dao replaces list, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.insertSamplers(mockStableDiffusionSamplers) }

        assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to insert samplers, dao throws exception during delete, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } throws stubException

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.insertSamplers(mockStableDiffusionSamplers) }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to insert samplers, dao throws exception during insertion, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } throws stubException

        val actual = runCatching { localDataSource.insertSamplers(mockStableDiffusionSamplers) }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
