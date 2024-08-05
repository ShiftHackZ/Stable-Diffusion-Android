package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockEmbeddings
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class EmbeddingsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRdsA1111 = mockk<EmbeddingsDataSource.Remote.Automatic1111>()
    private val stubRdsSwarm = mockk<EmbeddingsDataSource.Remote.SwarmUi>()
    private val stubSwarmSession = mockk<SwarmUiSessionDataSource>()
    private val stubLds = mockk<EmbeddingsDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    private val repository = EmbeddingsRepositoryImpl(
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
    fun `given attempt to fetch embeddings, source is AUTOMATIC1111, remote returns data, local insert success, expected complete value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchEmbeddings()
        } returns Single.just(mockEmbeddings)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.complete()

        repository
            .fetchEmbeddings()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, source is SWARM_UI, remote returns data, local insert success, expected complete value`() {
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
            stubRdsSwarm.fetchEmbeddings(any())
        } returns Single.just(mockEmbeddings)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.complete()

        repository
            .fetchEmbeddings()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, source is AUTOMATIC1111, remote throws exception, local insert success, expected error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchEmbeddings()
        } returns Single.error(stubException)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.complete()

        repository
            .fetchEmbeddings()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, source is SWARM_UI, remote throws exception, local insert success, expected error value`() {
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
            stubRdsSwarm.fetchEmbeddings(any())
        } returns Single.error(stubException)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.complete()

        repository
            .fetchEmbeddings()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, source is AUTOMATIC1111, remote returns data, local insert fails, expected error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchEmbeddings()
        } returns Single.just(mockEmbeddings)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.error(stubException)

        repository
            .fetchEmbeddings()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch embeddings, source is SWARM_UI, remote returns data, local insert fails, expected error value`() {
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
            stubRdsSwarm.fetchEmbeddings(any())
        } returns Single.just(mockEmbeddings)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.error(stubException)

        repository
            .fetchEmbeddings()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to get embeddings, local data source returns list, expected valid domain models list value`() {
        every {
            stubLds.getEmbeddings()
        } returns Single.just(mockEmbeddings)

        repository
            .getEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get embeddings, local data source returns empty list, expected empty domain models list value`() {
        every {
            stubLds.getEmbeddings()
        } returns Single.just(emptyList())

        repository
            .getEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(emptyList())
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to get embeddings, local data source throws exception, expected error value`() {
        every {
            stubLds.getEmbeddings()
        } returns Single.error(stubException)

        repository
            .getEmbeddings()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, source is AUTOMATIC1111, remote returns data, local returns data, expected valid domain models list value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchEmbeddings()
        } returns Single.just(mockEmbeddings)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.complete()

        every {
            stubLds.getEmbeddings()
        } returns Single.just(mockEmbeddings)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, source is SWARM_UI, remote returns data, local returns data, expected valid domain models list value`() {
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
            stubRdsSwarm.fetchEmbeddings(any())
        } returns Single.just(mockEmbeddings)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.complete()

        every {
            stubLds.getEmbeddings()
        } returns Single.just(mockEmbeddings)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, source is AUTOMATIC1111, remote fails, local returns data, expected valid domain models list value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchEmbeddings()
        } returns Single.error(stubException)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.complete()

        every {
            stubLds.getEmbeddings()
        } returns Single.just(mockEmbeddings)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, source is SWARM_UI, remote fails, local returns data, expected valid domain models list value`() {
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
            stubRdsA1111.fetchEmbeddings()
        } returns Single.error(stubException)

        every {
            stubLds.insertEmbeddings(any())
        } returns Completable.complete()

        every {
            stubLds.getEmbeddings()
        } returns Single.just(mockEmbeddings)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertNoErrors()
            .assertValue(mockEmbeddings)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, source is AUTOMATIC1111, remote fails, local fails, expected valid error value`() {
        every {
            stubPreferenceManager::source.get()
        } returns ServerSource.AUTOMATIC1111

        every {
            stubRdsA1111.fetchEmbeddings()
        } returns Single.error(stubException)

        every {
            stubLds.getEmbeddings()
        } returns Single.error(stubException)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to fetch and get embeddings, source is SWARM_UI, remote fails, local fails, expected valid error value`() {
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
            stubRdsA1111.fetchEmbeddings()
        } returns Single.error(stubException)

        every {
            stubLds.getEmbeddings()
        } returns Single.error(stubException)

        repository
            .fetchAndGetEmbeddings()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
