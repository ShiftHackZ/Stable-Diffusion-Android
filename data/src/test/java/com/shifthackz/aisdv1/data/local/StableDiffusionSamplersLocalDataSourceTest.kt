package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionSamplerEntities
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionSamplers
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionSamplerDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StableDiffusionSamplersLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<StableDiffusionSamplerDao>()
    
    private val localDataSource = StableDiffusionSamplersLocalDataSource(stubDao)

    @Test
    fun `given attempt to get samplers, dao returns list, expected valid domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(mockStableDiffusionSamplerEntities)

        localDataSource
            .getSamplers()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionSamplers)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get samplers, dao returns empty list, expected empty domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(emptyList())

        localDataSource
            .getSamplers()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get samplers, dao throws exception, expected error value`() {
        every {
            stubDao.queryAll()
        } returns Single.error(stubException)

        localDataSource
            .getSamplers()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert samplers, dao replaces list, expected complete value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertSamplers(mockStableDiffusionSamplers)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert samplers, dao throws exception during delete, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.error(stubException)

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertSamplers(mockStableDiffusionSamplers)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert samplers, dao throws exception during insertion, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.error(stubException)

        localDataSource
            .insertSamplers(mockStableDiffusionSamplers)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
