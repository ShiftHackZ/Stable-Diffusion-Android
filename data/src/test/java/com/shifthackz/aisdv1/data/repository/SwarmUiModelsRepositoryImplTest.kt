package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockSwarmUiModels
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class SwarmUiModelsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubSession = mockk<SwarmUiSessionDataSource>()
    private val stubRds = mockk<SwarmUiModelsDataSource.Remote>()
    private val stubLds = mockk<SwarmUiModelsDataSource.Local>()
    
    private val repository = SwarmUiModelsRepositoryImpl(stubSession, stubRds, stubLds)

    @Before
    fun initialize() {
        every {
            stubSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubSession.handleSessionError(any<Single<Any>>())
        } returnsArgument 0
    }

    @Test
    fun `given attempt to fetch models, remote returns data, local insert success, expected complete value`() {
        every {
            stubRds.fetchSwarmModels(any())
        } returns Single.just(mockSwarmUiModels)

        every {
            stubLds.insertModels(any())
        } returns Completable.complete()

        repository
            .fetchModels()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch models, remote throws exception, local insert success, expected error value`() {
        every {
            stubRds.fetchSwarmModels(any())
        } returns Single.error(stubException)

        every {
            stubLds.insertModels(any())
        } returns Completable.complete()

        repository
            .fetchModels()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch models, remote returns data, local insert fails, expected error value`() {
        every {
            stubRds.fetchSwarmModels(any())
        } returns Single.just(mockSwarmUiModels)

        every {
            stubLds.insertModels(any())
        } returns Completable.error(stubException)

        repository
            .fetchModels()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get models, local data source returns list, expected valid domain models list value`() {
        every {
            stubLds.getModels()
        } returns Single.just(mockSwarmUiModels)

        repository
            .getModels()
            .test()
            .assertNoErrors()
            .assertValue(mockSwarmUiModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get models, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLds.getModels()
        } returns Single.just(emptyList())

        repository
            .getModels()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get models, local data source throws exception, expected error value`() {
        every {
            stubLds.getModels()
        } returns Single.error(stubException)

        repository
            .getModels()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubRds.fetchSwarmModels(any())
        } returns Single.just(mockSwarmUiModels)

        every {
            stubLds.insertModels(any())
        } returns Completable.complete()

        every {
            stubLds.getModels()
        } returns Single.just(mockSwarmUiModels)

        repository
            .fetchAndGetModels()
            .test()
            .assertNoErrors()
            .assertValue(mockSwarmUiModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubRds.fetchSwarmModels(any())
        } returns Single.error(stubException)

        every {
            stubLds.insertModels(any())
        } returns Completable.complete()

        every {
            stubLds.getModels()
        } returns Single.just(mockSwarmUiModels)

        repository
            .fetchAndGetModels()
            .test()
            .assertNoErrors()
            .assertValue(mockSwarmUiModels)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local fails, expected valid error value`() {
        every {
            stubRds.fetchSwarmModels(any())
        } returns Single.error(stubException)

        every {
            stubLds.getModels()
        } returns Single.error(stubException)

        repository
            .fetchAndGetModels()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }    
}
