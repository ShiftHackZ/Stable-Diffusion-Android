package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.data.mocks.mockHuggingFaceModelEntities
import com.shifthackz.aisdv1.data.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.storage.db.persistent.dao.HuggingFaceModelDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class HuggingFaceModelsLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<HuggingFaceModelDao>()

    private val localDataSource = HuggingFaceModelsLocalDataSource(stubDao)

    @Test
    fun `given attempt to get all, dao returns list, expected valid domain model list value`() {
        every {
            stubDao.query()
        } returns Single.just(mockHuggingFaceModelEntities)

        localDataSource
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(mockHuggingFaceModelEntities.mapEntityToDomain())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get all, dao throws exception, expected error value`() {
        every {
            stubDao.query()
        } returns Single.error(stubException)

        localDataSource
            .getAll()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert list, dao insert success, expected complete value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .save(mockHuggingFaceModels)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert list, dao throws exception, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.error(stubException)

        localDataSource
            .save(mockHuggingFaceModels)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
