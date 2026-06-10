package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockEmbeddings
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionEmbeddingEntities
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionEmbeddingDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class EmbeddingsLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<StableDiffusionEmbeddingDao>()

    private val localDataSource = EmbeddingsLocalDataSource(stubDao)

    @Test
    fun `given attempt to get embeddings, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns mockStableDiffusionEmbeddingEntities

        val actual = localDataSource.getEmbeddings()

        Assert.assertEquals(mockEmbeddings, actual)
    }

    @Test
    fun `given attempt to get embeddings, dao returns empty list, expected empty domain model list value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } returns emptyList()

        val actual = localDataSource.getEmbeddings()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to get embeddings, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryAll()
        } throws stubException

        val actual = runCatching { localDataSource.getEmbeddings() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert embeddings, dao replaces list, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        localDataSource.insertEmbeddings(mockEmbeddings)

        coVerify {
            stubDao.deleteAll()
            stubDao.insertList(any())
        }
    }

    @Test
    fun `given attempt to insert embeddings, dao throws exception during delete, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } throws stubException

        coEvery {
            stubDao.insertList(any())
        } returns Unit

        val actual = runCatching { localDataSource.insertEmbeddings(mockEmbeddings) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to insert embeddings, dao throws exception during insertion, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        coEvery {
            stubDao.insertList(any())
        } throws stubException

        val actual = runCatching { localDataSource.insertEmbeddings(mockEmbeddings) }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
