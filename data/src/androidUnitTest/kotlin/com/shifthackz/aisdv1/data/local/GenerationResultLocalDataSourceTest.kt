package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResults
import com.shifthackz.aisdv1.data.mocks.mockGenerationResultEntities
import com.shifthackz.aisdv1.data.mocks.mockGenerationResultEntity
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GenerationResultLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<GenerationResultDao>()

    private val localDataSource = GenerationResultLocalDataSource(stubDao)

    @Test
    fun `given attempt to insert ai generation result, operation successful, expected id of inserted result value`() = runTest {
        coEvery {
            stubDao.insert(any())
        } returns mockAiGenerationResult.id

        val actual = localDataSource.insert(mockAiGenerationResult)

        Assert.assertEquals(mockAiGenerationResult.id, actual)
    }

    @Test
    fun `given attempt to insert ai generation result, operation failed, expected error value`() = runTest {
        coEvery {
            stubDao.insert(any())
        } throws stubException

        val actual = runCatching { localDataSource.insert(mockAiGenerationResult) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to query ai generation results, dao returns list, expected valid domain model list value`() = runTest {
        coEvery {
            stubDao.query()
        } returns mockGenerationResultEntities

        val actual = localDataSource.queryAll()

        Assert.assertEquals(mockAiGenerationResults, actual)
    }

    @Test
    fun `given attempt to query ai generation results, dao returns empty list, expected empty domain model list value`() = runTest {
        coEvery {
            stubDao.query()
        } returns emptyList()

        val actual = localDataSource.queryAll()

        Assert.assertEquals(emptyList<AiGenerationResult>(), actual)
    }

    @Test
    fun `given attempt to query ai generation results, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.query()
        } throws stubException

        val actual = runCatching { localDataSource.queryAll() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given two attempts to query by page, dao has only one page, expected valid page value, then empty page value`() = runTest {
        coEvery {
            stubDao.queryPage(20, 0)
        } returns (0 until 20).map { mockGenerationResultEntity }

        coEvery {
            stubDao.queryPage(20, 1)
        } returns emptyList()

        val firstPage = localDataSource.queryPage(20, 0)
        val secondPage = localDataSource.queryPage(20, 1)

        Assert.assertEquals(20, firstPage.size)
        Assert.assertEquals(emptyList<AiGenerationResult>(), secondPage)
    }

    @Test
    fun `given attempt to query by page, dao throws error, expected error value`() = runTest {
        coEvery {
            stubDao.queryPage(any(), any())
        } throws stubException

        val actual = runCatching { localDataSource.queryPage(20, 0) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to query by id, dao returns item, expected valid domain model value`() = runTest {
        coEvery {
            stubDao.queryById(5598L)
        } returns mockGenerationResultEntity

        val actual = localDataSource.queryById(5598L)

        Assert.assertEquals(mockAiGenerationResult, actual)
    }

    @Test
    fun `given attempt to query by id, dao throws exception, expected error value`() = runTest {
        coEvery {
            stubDao.queryById(5598L)
        } throws stubException

        val actual = runCatching { localDataSource.queryById(5598L) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to update visibility by id list, dao update succeeds, expected complete value`() = runTest {
        coEvery {
            stubDao.updateHiddenByIdList(listOf(5598L), true)
        } returns Unit

        val actual = runCatching { localDataSource.updateHiddenByIdList(listOf(5598L), true) }

        Assert.assertTrue(actual.isSuccess)
        coVerify {
            stubDao.updateHiddenByIdList(listOf(5598L), true)
        }
    }

    @Test
    fun `given attempt to update visibility by id list, dao update fails, expected error value`() = runTest {
        coEvery {
            stubDao.updateHiddenByIdList(any(), any())
        } throws stubException

        val actual = runCatching { localDataSource.updateHiddenByIdList(listOf(5598L), false) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to update liked by id list, dao update succeeds, expected complete value`() = runTest {
        coEvery {
            stubDao.updateLikedByIdList(listOf(5598L), true)
        } returns Unit

        val actual = runCatching { localDataSource.updateLikedByIdList(listOf(5598L), true) }

        Assert.assertTrue(actual.isSuccess)
        coVerify {
            stubDao.updateLikedByIdList(listOf(5598L), true)
        }
    }

    @Test
    fun `given attempt to update liked by id list, dao update fails, expected error value`() = runTest {
        coEvery {
            stubDao.updateLikedByIdList(any(), any())
        } throws stubException

        val actual = runCatching { localDataSource.updateLikedByIdList(listOf(5598L), false) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to delete by id list, dao deleted successfully, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteByIdList(any())
        } returns Unit

        val actual = runCatching { localDataSource.deleteByIdList(listOf(5598L)) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to delete by id list, dao delete failure, expected error value`() = runTest {
        coEvery {
            stubDao.deleteByIdList(any())
        } throws stubException

        val actual = runCatching { localDataSource.deleteByIdList(listOf(5598L)) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to delete by id, dao deleted successfully, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteById(any())
        } returns Unit

        val actual = runCatching { localDataSource.deleteById(5598L) }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to delete by id, dao delete failure, expected error value`() = runTest {
        coEvery {
            stubDao.deleteById(any())
        } throws stubException

        val actual = runCatching { localDataSource.deleteById(5598L) }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to delete all, dao deleted successfully, expected complete value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } returns Unit

        val actual = runCatching { localDataSource.deleteAll() }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to delete all, dao delete failure, expected error value`() = runTest {
        coEvery {
            stubDao.deleteAll()
        } throws stubException

        val actual = runCatching { localDataSource.deleteAll() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
