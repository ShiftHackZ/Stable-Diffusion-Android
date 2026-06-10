package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockServerConfiguration
import com.shifthackz.aisdv1.data.mocks.mockServerConfigurationEntity
import com.shifthackz.aisdv1.storage.db.cache.dao.ServerConfigurationDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServerConfigurationLocalDataSourceTest {

    private val stubException = RuntimeException("Database error.")
    private val stubDao = mockk<ServerConfigurationDao>()

    private val localDataSource = ServerConfigurationLocalDataSource(stubDao)

    @Test
    fun `given attempt to save server configuration, dao insert success, expected complete value`() = runTest {
        coEvery {
            stubDao.insert(any())
        } returns Unit

        val actual = runCatching { localDataSource.save(mockServerConfiguration) }

        assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to save server configuration, dao insert failed, expected error value`() = runTest {
        coEvery {
            stubDao.insert(any())
        } throws stubException

        val actual = runCatching { localDataSource.save(mockServerConfiguration) }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to get server configuration, dao returned record, expected valid domain value`() = runTest {
        coEvery {
            stubDao.query()
        } returns mockServerConfigurationEntity

        assertEquals(mockServerConfiguration, localDataSource.get())
    }

    @Test
    fun `given attempt to get server configuration, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.query()
        } throws stubException

        val actual = runCatching { localDataSource.get() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
