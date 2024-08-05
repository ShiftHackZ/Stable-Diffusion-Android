package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionLoraEntities
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionLoras
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionLoraDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class LorasLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<StableDiffusionLoraDao>()
    
    private val localDataSource = LorasLocalDataSource(stubDao)

    @Test
    fun `given attempt to get loras, dao returns list, expected valid domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(mockStableDiffusionLoraEntities)

        localDataSource
            .getLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get loras, dao returns empty list, expected empty domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(emptyList())

        localDataSource
            .getLoras()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get loras, dao throws exception, expected error value`() {
        every {
            stubDao.queryAll()
        } returns Single.error(stubException)

        localDataSource
            .getLoras()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert loras, dao replaces list, expected complete value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertLoras(mockStableDiffusionLoras)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert loras, dao throws exception during delete, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.error(stubException)

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertLoras(mockStableDiffusionLoras)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert loras, dao throws exception during insertion, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.error(stubException)

        localDataSource
            .insertLoras(mockStableDiffusionLoras)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
