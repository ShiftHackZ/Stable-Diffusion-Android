package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockServerConfiguration
import com.shifthackz.aisdv1.data.mocks.mockServerConfigurationEntity
import com.shifthackz.aisdv1.storage.db.cache.dao.ServerConfigurationDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class ServerConfigurationLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<ServerConfigurationDao>()

    private val localDataSource = ServerConfigurationLocalDataSource(stubDao)

    @Test
    fun `given attempt to save server configuration, dao insert success, expected complete value`() {
        every {
            stubDao.insert(any())
        } returns Completable.complete()

        localDataSource
            .save(mockServerConfiguration)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to save server configuration, dao insert failed, expected error value`() {
        every {
            stubDao.insert(any())
        } returns Completable.error(stubException)

        localDataSource
            .save(mockServerConfiguration)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get server configuration, dao returned record, expected valid domain value`() {
        every {
            stubDao.query()
        } returns Single.just(mockServerConfigurationEntity)

        localDataSource
            .get()
            .test()
            .assertNoErrors()
            .assertValue(mockServerConfiguration)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get server configuration, dao throws exception, expected error value`() {
        every {
            stubDao.query()
        } returns Single.error(stubException)

        localDataSource
            .get()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
