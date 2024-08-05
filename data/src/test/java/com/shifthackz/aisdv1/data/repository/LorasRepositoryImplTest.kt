package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionLoras
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class LorasRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRdsA1111 = mockk<LorasDataSource.Remote.Automatic1111>()
    private val stubRdsSwarm = mockk<LorasDataSource.Remote.SwarmUi>()
    private val stubSwarmSession = mockk<SwarmUiSessionDataSource>()
    private val stubLds = mockk<LorasDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    private val repository = LorasRepositoryImpl(
        rdsA1111 = stubRdsA1111,
        rdsSwarm = stubRdsSwarm,
        swarmSession = stubSwarmSession,
        lds = stubLds,
        preferenceManager = stubPreferenceManager,
    )

    @Before
    fun initialize() {
        every {
            stubSwarmSession.handleSessionError(any<Single<Any>>())
        } returnsArgument 0
    }

    @Test
    fun `given attempt to fetch loras, source is AUTOMATIC1111, remote returns data, local insert success, expected complete value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchLoras()
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLds.insertLoras(any())
        } returns Completable.complete()

        repository
            .fetchLoras()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch loras, source is SWARM_UI, remote returns data, local insert success, expected complete value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.SWARM_UI

        every {
            stubSwarmSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSwarmSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRdsSwarm.fetchLoras(any())
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLds.insertLoras(any())
        } returns Completable.complete()

        repository
            .fetchLoras()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch loras, source is AUTOMATIC1111, remote throws exception, local insert success, expected error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchLoras()
        } returns Single.error(stubException)

        every {
            stubLds.insertLoras(any())
        } returns Completable.complete()

        repository
            .fetchLoras()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch loras, source is SWARM_UI, remote throws exception, local insert success, expected error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.SWARM_UI

        every {
            stubSwarmSession.getSessionId(any())
        } returns Single.error(stubException)

        every {
            stubSwarmSession.getSessionId()
        } returns Single.error(stubException)

        every {
            stubRdsSwarm.fetchLoras(any())
        } returns Single.error(stubException)

        every {
            stubLds.insertLoras(any())
        } returns Completable.complete()

        repository
            .fetchLoras()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch loras, source is AUTOMATIC1111, remote returns data, local insert fails, expected error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchLoras()
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLds.insertLoras(any())
        } returns Completable.error(stubException)

        repository
            .fetchLoras()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch loras, source is SWARM_UI, remote returns data, local insert fails, expected error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.SWARM_UI

        every {
            stubSwarmSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSwarmSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRdsSwarm.fetchLoras(any())
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLds.insertLoras(any())
        } returns Completable.error(stubException)

        repository
            .fetchLoras()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get loras, local data source returns list, expected valid domain models list value`() {
        every {
            stubLds.getLoras()
        } returns Single.just(mockStableDiffusionLoras)

        repository
            .getLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get loras, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLds.getLoras()
        } returns Single.just(emptyList())

        repository
            .getLoras()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get loras, local data source throws exception, expected error value`() {
        every {
            stubLds.getLoras()
        } returns Single.error(stubException)

        repository
            .getLoras()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, source is AUTOMATIC1111, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchLoras()
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLds.insertLoras(any())
        } returns Completable.complete()

        every {
            stubLds.getLoras()
        } returns Single.just(mockStableDiffusionLoras)

        repository
            .fetchAndGetLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, source is SWARM_UI, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.SWARM_UI

        every {
            stubSwarmSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSwarmSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRdsSwarm.fetchLoras(any())
        } returns Single.just(mockStableDiffusionLoras)

        every {
            stubLds.insertLoras(any())
        } returns Completable.complete()

        every {
            stubLds.getLoras()
        } returns Single.just(mockStableDiffusionLoras)

        repository
            .fetchAndGetLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, source is AUTOMATIC1111, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchLoras()
        } returns Single.error(stubException)

        every {
            stubLds.insertLoras(any())
        } returns Completable.complete()

        every {
            stubLds.getLoras()
        } returns Single.just(mockStableDiffusionLoras)

        repository
            .fetchAndGetLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, source is SWARM_UI, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.SWARM_UI

        every {
            stubSwarmSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSwarmSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRdsSwarm.fetchLoras(any())
        } returns Single.error(stubException)

        every {
            stubLds.insertLoras(any())
        } returns Completable.complete()

        every {
            stubLds.getLoras()
        } returns Single.just(mockStableDiffusionLoras)

        repository
            .fetchAndGetLoras()
            .test()
            .assertNoErrors()
            .assertValue(mockStableDiffusionLoras)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, source is AUTOMATIC1111, remote fails, local fails, expected valid error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchLoras()
        } returns Single.error(stubException)

        every {
            stubLds.getLoras()
        } returns Single.error(stubException)

        repository
            .fetchAndGetLoras()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get loras, source is SWARM_UI, remote fails, local fails, expected valid error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.SWARM_UI

        every {
            stubSwarmSession.getSessionId(any())
        } returns Single.just("5598")

        every {
            stubSwarmSession.getSessionId()
        } returns Single.just("5598")

        every {
            stubRdsSwarm.fetchLoras(any())
        } returns Single.error(stubException)

        every {
            stubLds.getLoras()
        } returns Single.error(stubException)

        repository
            .fetchAndGetLoras()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
