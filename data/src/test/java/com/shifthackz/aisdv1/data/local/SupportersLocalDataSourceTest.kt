package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockSupporterEntities
import com.shifthackz.aisdv1.data.mocks.mockSupporters
import com.shifthackz.aisdv1.storage.db.persistent.dao.SupporterDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class SupportersLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<SupporterDao>()

    private val localDataSource = SupportersLocalDataSource(stubDao)

    @Test
    fun `given attempt to get supporters, dao returns list, expected valid domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(mockSupporterEntities)

        localDataSource
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue { mockSupporters.size == it.size }
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get supporters, dao returns empty list, expected empty domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(emptyList())

        localDataSource
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get supporters, dao throws exception, expected error value`() {
        every {
            stubDao.queryAll()
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
    fun `given attempt to insert supporters, dao replaces list, expected complete value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .save(mockSupporters)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert supporters, dao throws exception during delete, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.error(stubException)

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .save(mockSupporters)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert supporters, dao throws exception during insertion, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.error(stubException)

        localDataSource
            .save(mockSupporters)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
