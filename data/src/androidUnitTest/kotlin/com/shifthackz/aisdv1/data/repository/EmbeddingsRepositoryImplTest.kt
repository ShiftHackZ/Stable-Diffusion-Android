package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockEmbeddings
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EmbeddingsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRdsA1111 = mockk<EmbeddingsDataSource.Remote.Automatic1111>()
    private val stubRdsSwarm = mockk<EmbeddingsDataSource.Remote.SwarmUi>()
    private val stubSwarmSessionRemote = mockk<SwarmUiModelsRemoteDataSource>()
    private val stubLds = mockk<EmbeddingsDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubSessionPreference = mockk<SessionPreference>(relaxed = true)
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val repository = EmbeddingsRepositoryImpl(
        rdsA1111 = stubRdsA1111,
        rdsSwarm = stubRdsSwarm,
        swarmSessionRemoteDataSource = stubSwarmSessionRemote,
        lds = stubLds,
        preferenceManager = stubPreferenceManager,
        sessionPreference = stubSessionPreference,
        authorizationStore = stubAuthorizationStore,
    )

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.automatic1111ServerUrl
        } returns A1111_URL

        every {
            stubPreferenceManager.swarmUiServerUrl
        } returns SWARM_URL

        every {
            stubAuthorizationStore.getAuthorizationCredentials()
        } returns AuthorizationCredentials.None

        every {
            stubSessionPreference.swarmUiSessionId
        } returns SESSION_ID
    }

    @Test
    fun `given attempt to fetch embeddings, source is AUTOMATIC1111, remote returns data, local insert success, expected complete value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchEmbeddings(A1111_URL, AuthorizationCredentials.None)
        } returns mockEmbeddings

        coEvery {
            stubLds.insertEmbeddings(any())
        } returns Unit

        repository.fetchEmbeddings()

        coVerify {
            stubLds.insertEmbeddings(mockEmbeddings)
        }
    }

    @Test
    fun `given attempt to fetch embeddings, source is SWARM_UI, remote returns data, local insert success, expected complete value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

        coEvery {
            stubRdsSwarm.fetchEmbeddings(SWARM_URL, SESSION_ID, AuthorizationCredentials.None)
        } returns mockEmbeddings

        coEvery {
            stubLds.insertEmbeddings(any())
        } returns Unit

        repository.fetchEmbeddings()

        coVerify {
            stubLds.insertEmbeddings(mockEmbeddings)
        }
    }

    @Test
    fun `given attempt to fetch embeddings with empty swarm session, expected new session requested before fetch`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

        every {
            stubSessionPreference.swarmUiSessionId
        } returns ""

        coEvery {
            stubSwarmSessionRemote.getNewSession(SWARM_URL, AuthorizationCredentials.None)
        } returns RENEWED_SESSION_ID

        coEvery {
            stubRdsSwarm.fetchEmbeddings(SWARM_URL, RENEWED_SESSION_ID, AuthorizationCredentials.None)
        } returns mockEmbeddings

        coEvery {
            stubLds.insertEmbeddings(any())
        } returns Unit

        repository.fetchEmbeddings()

        coVerify {
            stubSwarmSessionRemote.getNewSession(SWARM_URL, AuthorizationCredentials.None)
            stubLds.insertEmbeddings(mockEmbeddings)
        }
        verify {
            stubSessionPreference.swarmUiSessionId = RENEWED_SESSION_ID
        }
    }

    @Test
    fun `given attempt to fetch embeddings with bad swarm session, expected session renewed and fetch retried`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

        coEvery {
            stubRdsSwarm.fetchEmbeddings(SWARM_URL, SESSION_ID, AuthorizationCredentials.None)
        } throws SwarmUiBadSessionException()

        coEvery {
            stubSwarmSessionRemote.getNewSession(SWARM_URL, AuthorizationCredentials.None)
        } returns RENEWED_SESSION_ID

        coEvery {
            stubRdsSwarm.fetchEmbeddings(SWARM_URL, RENEWED_SESSION_ID, AuthorizationCredentials.None)
        } returns mockEmbeddings

        coEvery {
            stubLds.insertEmbeddings(any())
        } returns Unit

        repository.fetchEmbeddings()

        coVerify {
            stubLds.insertEmbeddings(mockEmbeddings)
        }
        verify {
            stubSessionPreference.swarmUiSessionId = RENEWED_SESSION_ID
        }
    }

    @Test
    fun `given attempt to fetch embeddings, remote throws exception, expected error value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchEmbeddings(A1111_URL, AuthorizationCredentials.None)
        } throws stubException

        val actual = runCatching { repository.fetchEmbeddings() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch embeddings, remote returns data, local insert fails, expected error value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchEmbeddings(A1111_URL, AuthorizationCredentials.None)
        } returns mockEmbeddings

        coEvery {
            stubLds.insertEmbeddings(any())
        } throws stubException

        val actual = runCatching { repository.fetchEmbeddings() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get embeddings, local data source returns list, expected valid domain models list value`() = runTest {
        coEvery {
            stubLds.getEmbeddings()
        } returns mockEmbeddings

        val actual = repository.getEmbeddings()

        Assert.assertEquals(mockEmbeddings, actual)
    }

    @Test
    fun `given attempt to get embeddings, local data source throws exception, expected error value`() = runTest {
        coEvery {
            stubLds.getEmbeddings()
        } throws stubException

        val actual = runCatching { repository.getEmbeddings() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch and get embeddings, remote returns data, local returns data, expected valid domain models list value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchEmbeddings(A1111_URL, AuthorizationCredentials.None)
        } returns mockEmbeddings

        coEvery {
            stubLds.insertEmbeddings(any())
        } returns Unit

        coEvery {
            stubLds.getEmbeddings()
        } returns mockEmbeddings

        val actual = repository.fetchAndGetEmbeddings()

        Assert.assertEquals(mockEmbeddings, actual)
    }

    @Test
    fun `given attempt to fetch and get embeddings, remote fails, local returns data, expected valid domain models list value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchEmbeddings(A1111_URL, AuthorizationCredentials.None)
        } throws stubException

        coEvery {
            stubLds.getEmbeddings()
        } returns mockEmbeddings

        val actual = repository.fetchAndGetEmbeddings()

        Assert.assertEquals(mockEmbeddings, actual)
    }

    @Test
    fun `given attempt to fetch and get embeddings, remote fails, local fails, expected valid error value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchEmbeddings(A1111_URL, AuthorizationCredentials.None)
        } throws stubException

        coEvery {
            stubLds.getEmbeddings()
        } throws stubException

        val actual = runCatching { repository.fetchAndGetEmbeddings() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val A1111_URL = "http://192.168.0.1:7860"
        const val SWARM_URL = "http://192.168.0.1:7801"
        const val SESSION_ID = "5598"
        const val RENEWED_SESSION_ID = "151297"
    }
}
