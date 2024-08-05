package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockSwarmUiModelEntities
import com.shifthackz.aisdv1.data.mocks.mockSwarmUiModels
import com.shifthackz.aisdv1.storage.db.cache.dao.SwarmUiModelDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class SwarmUiModelsLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<SwarmUiModelDao>()

    private val localDataSource = SwarmUiModelsLocalDataSource(stubDao)

    @Test
    fun `given attempt to get models, dao returns list, expected valid domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(mockSwarmUiModelEntities)

        localDataSource
            .getModels()
            .test()
            .assertNoErrors()
            .assertValue { it.size == mockSwarmUiModels.size }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get models, dao returns empty list, expected empty domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(emptyList())

        localDataSource
            .getModels()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get models, dao throws exception, expected error value`() {
        every {
            stubDao.queryAll()
        } returns Single.error(stubException)

        localDataSource
            .getModels()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert models, dao replaces list, expected complete value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertModels(mockSwarmUiModels)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert models, dao throws exception during delete, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.error(stubException)

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertModels(mockSwarmUiModels)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert models, dao throws exception during insertion, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.error(stubException)

        localDataSource
            .insertModels(mockSwarmUiModels)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
