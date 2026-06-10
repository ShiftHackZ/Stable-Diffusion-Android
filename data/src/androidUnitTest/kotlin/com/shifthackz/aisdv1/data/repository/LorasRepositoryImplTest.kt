package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionLoras
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
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

class LorasRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRdsA1111 = mockk<LorasDataSource.Remote.Automatic1111>()
    private val stubRdsSwarm = mockk<LorasDataSource.Remote.SwarmUi>()
    private val stubSwarmSessionRemote = mockk<SwarmUiModelsRemoteDataSource>()
    private val stubLds = mockk<LorasDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubSessionPreference = mockk<SessionPreference>(relaxed = true)
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val repository = LorasRepositoryImpl(
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
    fun `given attempt to fetch loras, source is AUTOMATIC1111, remote returns data, local insert success, expected complete value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchLoras(A1111_URL, AuthorizationCredentials.None)
        } returns mockStableDiffusionLoras

        coEvery {
            stubLds.insertLoras(any())
        } returns Unit

        repository.fetchLoras()

        coVerify {
            stubLds.insertLoras(mockStableDiffusionLoras)
        }
    }

    @Test
    fun `given attempt to fetch loras, source is SWARM_UI, remote returns data, local insert success, expected complete value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

        coEvery {
            stubRdsSwarm.fetchLoras(SWARM_URL, SESSION_ID, AuthorizationCredentials.None)
        } returns mockStableDiffusionLoras

        coEvery {
            stubLds.insertLoras(any())
        } returns Unit

        repository.fetchLoras()

        coVerify {
            stubLds.insertLoras(mockStableDiffusionLoras)
        }
    }

    @Test
    fun `given attempt to fetch loras with empty swarm session, expected new session requested before fetch`() = runTest {
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
            stubRdsSwarm.fetchLoras(SWARM_URL, RENEWED_SESSION_ID, AuthorizationCredentials.None)
        } returns mockStableDiffusionLoras

        coEvery {
            stubLds.insertLoras(any())
        } returns Unit

        repository.fetchLoras()

        coVerify {
            stubSwarmSessionRemote.getNewSession(SWARM_URL, AuthorizationCredentials.None)
            stubLds.insertLoras(mockStableDiffusionLoras)
        }
        verify {
            stubSessionPreference.swarmUiSessionId = RENEWED_SESSION_ID
        }
    }

    @Test
    fun `given attempt to fetch loras with bad swarm session, expected session renewed and fetch retried`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

        coEvery {
            stubRdsSwarm.fetchLoras(SWARM_URL, SESSION_ID, AuthorizationCredentials.None)
        } throws SwarmUiBadSessionException()

        coEvery {
            stubSwarmSessionRemote.getNewSession(SWARM_URL, AuthorizationCredentials.None)
        } returns RENEWED_SESSION_ID

        coEvery {
            stubRdsSwarm.fetchLoras(SWARM_URL, RENEWED_SESSION_ID, AuthorizationCredentials.None)
        } returns mockStableDiffusionLoras

        coEvery {
            stubLds.insertLoras(any())
        } returns Unit

        repository.fetchLoras()

        coVerify {
            stubLds.insertLoras(mockStableDiffusionLoras)
        }
        verify {
            stubSessionPreference.swarmUiSessionId = RENEWED_SESSION_ID
        }
    }

    @Test
    fun `given attempt to fetch loras, remote throws exception, expected error value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchLoras(A1111_URL, AuthorizationCredentials.None)
        } throws stubException

        val actual = runCatching { repository.fetchLoras() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch loras, remote returns data, local insert fails, expected error value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchLoras(A1111_URL, AuthorizationCredentials.None)
        } returns mockStableDiffusionLoras

        coEvery {
            stubLds.insertLoras(any())
        } throws stubException

        val actual = runCatching { repository.fetchLoras() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get loras, local data source returns list, expected valid domain models list value`() = runTest {
        coEvery {
            stubLds.getLoras()
        } returns mockStableDiffusionLoras

        val actual = repository.getLoras()

        Assert.assertEquals(mockStableDiffusionLoras, actual)
    }

    @Test
    fun `given attempt to get loras, local data source throws exception, expected error value`() = runTest {
        coEvery {
            stubLds.getLoras()
        } throws stubException

        val actual = runCatching { repository.getLoras() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch and get loras, remote returns data, local returns data, expected valid domain models list value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchLoras(A1111_URL, AuthorizationCredentials.None)
        } returns mockStableDiffusionLoras

        coEvery {
            stubLds.insertLoras(any())
        } returns Unit

        coEvery {
            stubLds.getLoras()
        } returns mockStableDiffusionLoras

        val actual = repository.fetchAndGetLoras()

        Assert.assertEquals(mockStableDiffusionLoras, actual)
    }

    @Test
    fun `given attempt to fetch and get loras, remote fails, local returns data, expected valid domain models list value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchLoras(A1111_URL, AuthorizationCredentials.None)
        } throws stubException

        coEvery {
            stubLds.getLoras()
        } returns mockStableDiffusionLoras

        val actual = repository.fetchAndGetLoras()

        Assert.assertEquals(mockStableDiffusionLoras, actual)
    }

    @Test
    fun `given attempt to fetch and get loras, remote fails, local fails, expected valid error value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRdsA1111.fetchLoras(A1111_URL, AuthorizationCredentials.None)
        } throws stubException

        coEvery {
            stubLds.getLoras()
        } throws stubException

        val actual = runCatching { repository.fetchAndGetLoras() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val A1111_URL = "http://192.168.0.1:7860"
        const val SWARM_URL = "http://192.168.0.1:7801"
        const val SESSION_ID = "5598"
        const val RENEWED_SESSION_ID = "151297"
    }
}
