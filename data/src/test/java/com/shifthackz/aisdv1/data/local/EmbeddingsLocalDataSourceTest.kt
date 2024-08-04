package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockEmbeddings
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionEmbeddingEntities
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionEmbeddingDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class EmbeddingsLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<StableDiffusionEmbeddingDao>()

    private val localDataSource = EmbeddingsLocalDataSource(stubDao)

    @Test
    fun `given attempt to get embeddings, dao returns list, expected valid domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(mockStableDiffusionEmbeddingEntities)

        localDataSource
            .getEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get embeddings, dao returns empty list, expected empty domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(emptyList())

        localDataSource
            .getEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get embeddings, dao throws exception, expected error value`() {
        every {
            stubDao.queryAll()
        } returns Single.error(stubException)

        localDataSource
            .getEmbeddings()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert embeddings, dao replaces list, expected complete value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertEmbeddings(mockEmbeddings)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert embeddings, dao throws exception during delete, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.error(stubException)

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertEmbeddings(mockEmbeddings)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert embeddings, dao throws exception during insertion, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.error(stubException)

        localDataSource
            .insertEmbeddings(mockEmbeddings)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
