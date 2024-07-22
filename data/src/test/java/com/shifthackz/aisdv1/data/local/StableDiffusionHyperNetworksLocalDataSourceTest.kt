package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionHyperNetworkEntities
import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionHyperNetworks
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionHyperNetworkDao
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class StableDiffusionHyperNetworksLocalDataSourceTest {

    private val stubException = Throwable("Database error.")
    private val stubDao = mockk<StableDiffusionHyperNetworkDao>()

    private val localDataSource = StableDiffusionHyperNetworksLocalDataSource(stubDao)

    @Test
    fun `given attempt to get hypernetworks, dao returns list, expected valid domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(mockStableDiffusionHyperNetworkEntities)

        localDataSource
            .getHyperNetworks()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionHyperNetworks)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get hypernetworks, dao returns empty list, expected empty domain model list value`() {
        every {
            stubDao.queryAll()
        } returns Single.just(emptyList())

        localDataSource
            .getHyperNetworks()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get hypernetworks, dao throws exception, expected error value`() {
        every {
            stubDao.queryAll()
        } returns Single.error(stubException)

        localDataSource
            .getHyperNetworks()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert hypernetworks, dao replaces list, expected complete value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertHyperNetworks(mockStableDiffusionHyperNetworks)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to insert hypernetworks, dao throws exception during delete, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.error(stubException)

        every {
            stubDao.insertList(any())
        } returns Completable.complete()

        localDataSource
            .insertHyperNetworks(mockStableDiffusionHyperNetworks)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to insert hypernetworks, dao throws exception during insertion, expected error value`() {
        every {
            stubDao.deleteAll()
        } returns Completable.complete()

        every {
            stubDao.insertList(any())
        } returns Completable.error(stubException)

        localDataSource
            .insertHyperNetworks(mockStableDiffusionHyperNetworks)
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
