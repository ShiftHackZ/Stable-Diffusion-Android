package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockSupporters
import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class SupportersRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRds = mockk<SupportersDataSource.Remote>()
    private val stubLds = mockk<SupportersDataSource.Local>()
    
    private val repository = SupportersRepositoryImpl(stubRds, stubLds)

    @Test
    fun `given attempt to fetch supporters, remote returns data, local insert success, expected complete value`() {
        every {
            stubRds.fetch()
        } returns Single.just(mockSupporters)

        every {
            stubLds.save(any())
        } returns Completable.complete()

        repository
            .fetchSupporters()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch supporters, remote throws exception, local insert success, expected error value`() {
        every {
            stubRds.fetch()
        } returns Single.error(stubException)

        every {
            stubLds.save(any())
        } returns Completable.complete()

        repository
            .fetchSupporters()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch supporters, remote returns data, local insert fails, expected error value`() {
        every {
            stubRds.fetch()
        } returns Single.just(mockSupporters)

        every {
            stubLds.save(any())
        } returns Completable.error(stubException)

        repository
            .fetchSupporters()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get supporters, local data source returns list, expected valid domain models list value`() {
        every {
            stubLds.getAll()
        } returns Single.just(mockSupporters)

        repository
            .getSupporters()
            .test()
            .assertNoErrors()
            .assertValue(mockSupporters)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get supporters, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLds.getAll()
        } returns Single.just(emptyList())

        repository
            .getSupporters()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get supporters, local data source throws exception, expected error value`() {
        every {
            stubLds.getAll()
        } returns Single.error(stubException)

        repository
            .getSupporters()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get supporters, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubRds.fetch()
        } returns Single.just(mockSupporters)

        every {
            stubLds.save(any())
        } returns Completable.complete()

        every {
            stubLds.getAll()
        } returns Single.just(mockSupporters)

        repository
            .fetchAndGetSupporters()
            .test()
            .assertNoErrors()
            .assertValue(mockSupporters)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get supporters, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubRds.fetch()
        } returns Single.error(stubException)

        every {
            stubLds.save(any())
        } returns Completable.complete()

        every {
            stubLds.getAll()
        } returns Single.just(mockSupporters)

        repository
            .fetchAndGetSupporters()
            .test()
            .assertNoErrors()
            .assertValue(mockSupporters)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get supporters, remote fails, local fails, expected valid error value`() {
        every {
            stubRds.fetch()
        } returns Single.error(stubException)

        every {
            stubLds.getAll()
        } returns Single.error(stubException)

        repository
            .fetchAndGetSupporters()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
