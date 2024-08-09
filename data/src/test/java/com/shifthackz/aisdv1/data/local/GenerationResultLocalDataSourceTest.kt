package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResults
import com.shifthackz.aisdv1.data.mocks.mockGenerationResultEntities
import com.shifthackz.aisdv1.data.mocks.mockGenerationResultEntity
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GenerationResultLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<GenerationResultDao>()

    private val localDataSource = GenerationResultLocalDataSource(stubDao)

    @Test
    fun `given attempt to insert ai generation result, operation successful, expected id of inserted result value`() {
        every {
            stubDao.insert(any())
        } returns Single.just(mockAiGenerationResult.id)

        localDataSource
            .insert(mockAiGenerationResult)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult.id)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert ai generation result, operation failed, expected error value`() {
        every {
            stubDao.insert(any())
        } returns Single.error(stubException)

        localDataSource
            .insert(mockAiGenerationResult)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to query ai generation results, dao returns list, expected valid domain model list value`() {
        every {
            stubDao.query()
        } returns Single.just(mockGenerationResultEntities)

        localDataSource
            .queryAll()
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResults)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to query ai generation results, dao returns empty list, expected empty domain model list value`() {
        every {
            stubDao.query()
        } returns Single.just(emptyList())

        localDataSource
            .queryAll()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to query ai generation results, dao throws exception, expected error value`() {
        every {
            stubDao.query()
        } returns Single.error(stubException)

        localDataSource
            .queryAll()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given two attempts to query by page, dao has only one page, expected valid page value, then empty page value`() {
        every {
            stubDao.queryPage(20, 0)
        } returns Single.just((0 until 20).map { mockGenerationResultEntity })

        every {
            stubDao.queryPage(20, 1)
        } returns Single.just(emptyList())

        localDataSource
            .queryPage(20, 0)
            .test()
            .assertNoErrors()
            .assertValue { actual -> actual is List<AiGenerationResult> && actual.size == 20 }
            .await()
            .assertComplete()

        localDataSource
            .queryPage(20, 1)
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to query by page, dao throws error, expected error value`() {
        every {
            stubDao.queryPage(any(), any())
        } returns Single.error(stubException)

        localDataSource
            .queryPage(20, 0)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to query by id, dao returns item, expected valid domain model value`() {
        every {
            stubDao.queryById(5598L)
        } returns Single.just(mockGenerationResultEntity)

        localDataSource
            .queryById(5598L)
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to query by id, dao throws exception, expected error value`() {
        every {
            stubDao.queryById(5598L)
        } returns Single.error(stubException)

        localDataSource
            .queryById(5598L)
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to delete by id list, dao deleted successfully, expected complete value`() {
        every {
            stubDao.deleteByIdList(any())
        } returns Completable.complete()

        localDataSource
            .deleteByIdList(listOf(5598L))
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to delete by id list, dao delete failure, expected error value`() {
        every {
            stubDao.deleteByIdList(any())
        } returns Completable.error(stubException)

        localDataSource
            .deleteByIdList(listOf(5598L))
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to delete by id, dao deleted successfully, expected complete value`() {
        every {
            stubDao.deleteById(any())
        } returns Completable.complete()

        localDataSource
            .deleteById(5598L)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to delete by id, dao delete failure, expected error value`() {
        every {
            stubDao.deleteById(any())
        } returns Completable.error(stubException)

        localDataSource
            .deleteById(5598L)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to delete all, dao deleted successfully, expected complete value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        localDataSource
            .deleteAll()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to delete all, dao delete failure, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.error(stubException)

        localDataSource
            .deleteAll()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
