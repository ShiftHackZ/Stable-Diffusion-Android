package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockSwarmUiModels
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SwarmUiModelsRepositoryImplTest {

    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<SwarmUiModelsRemoteDataSource>()
    private val stubLocalDataSource = mockk<SwarmUiModelsDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubSessionPreference = mockk<SessionPreference>(relaxed = true)
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val repository = SwarmUiModelsRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
        sessionPreference = stubSessionPreference,
        authorizationStore = stubAuthorizationStore,
    )

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.swarmUiServerUrl
        } returns BASE_URL

        every {
            stubAuthorizationStore.getAuthorizationCredentials()
        } returns AuthorizationCredentials.None

        every {
            stubSessionPreference.swarmUiSessionId
        } returns SESSION_ID
    }

    @Test
    fun `given attempt to fetch models, remote returns data, local insert success, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, SESSION_ID, AuthorizationCredentials.None)
        } returns mockSwarmUiModels

        coEvery {
            stubLocalDataSource.insertModels(any())
        } returns Unit

        repository.fetchModels()

        coVerify {
            stubLocalDataSource.insertModels(mockSwarmUiModels)
        }
    }

    @Test
    fun `given attempt to fetch models with empty session, expected new session requested before fetch`() = runTest {
        every {
            stubSessionPreference.swarmUiSessionId
        } returns ""

        coEvery {
            stubRemoteDataSource.getNewSession(BASE_URL, AuthorizationCredentials.None)
        } returns RENEWED_SESSION_ID

        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, RENEWED_SESSION_ID, AuthorizationCredentials.None)
        } returns mockSwarmUiModels

        coEvery {
            stubLocalDataSource.insertModels(any())
        } returns Unit

        repository.fetchModels()

        coVerify {
            stubRemoteDataSource.getNewSession(BASE_URL, AuthorizationCredentials.None)
            stubLocalDataSource.insertModels(mockSwarmUiModels)
        }
    }

    @Test
    fun `given attempt to fetch models with bad session, expected session renewed and fetch retried`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, SESSION_ID, AuthorizationCredentials.None)
        } throws SwarmUiBadSessionException()

        coEvery {
            stubRemoteDataSource.getNewSession(BASE_URL, AuthorizationCredentials.None)
        } returns RENEWED_SESSION_ID

        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, RENEWED_SESSION_ID, AuthorizationCredentials.None)
        } returns mockSwarmUiModels

        coEvery {
            stubLocalDataSource.insertModels(any())
        } returns Unit

        repository.fetchModels()

        coVerify {
            stubSessionPreference.swarmUiSessionId = RENEWED_SESSION_ID
            stubLocalDataSource.insertModels(mockSwarmUiModels)
        }
    }

    @Test
    fun `given attempt to fetch models, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, SESSION_ID, AuthorizationCredentials.None)
        } throws stubException

        val actual = runCatching { repository.fetchModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch models, remote returns data, local insert fails, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, SESSION_ID, AuthorizationCredentials.None)
        } returns mockSwarmUiModels

        coEvery {
            stubLocalDataSource.insertModels(any())
        } throws stubException

        val actual = runCatching { repository.fetchModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to get models, local data source returns list, expected valid domain models list value`() = runTest {
        coEvery {
            stubLocalDataSource.getModels()
        } returns mockSwarmUiModels

        val actual = repository.getModels()

        Assert.assertEquals(mockSwarmUiModels, actual)
    }

    @Test
    fun `given attempt to get models, local data source returns empty list, expected empty domain models list value`() = runTest {
        coEvery {
            stubLocalDataSource.getModels()
        } returns emptyList()

        val actual = repository.getModels()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given attempt to get models, local data source throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.getModels()
        } throws stubException

        val actual = runCatching { repository.getModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to fetch and get models, remote returns data, local returns data, expected valid domain models list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, SESSION_ID, AuthorizationCredentials.None)
        } returns mockSwarmUiModels

        coEvery {
            stubLocalDataSource.insertModels(any())
        } returns Unit

        coEvery {
            stubLocalDataSource.getModels()
        } returns mockSwarmUiModels

        val actual = repository.fetchAndGetModels()

        Assert.assertEquals(mockSwarmUiModels, actual)
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local returns data, expected valid domain models list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, SESSION_ID, AuthorizationCredentials.None)
        } throws stubException

        coEvery {
            stubLocalDataSource.getModels()
        } returns mockSwarmUiModels

        val actual = repository.fetchAndGetModels()

        Assert.assertEquals(mockSwarmUiModels, actual)
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local fails, expected valid error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSwarmModels(BASE_URL, SESSION_ID, AuthorizationCredentials.None)
        } throws stubException

        coEvery {
            stubLocalDataSource.getModels()
        } throws stubException

        val actual = runCatching { repository.fetchAndGetModels() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    private companion object {
        const val BASE_URL = "http://192.168.0.1:7801"
        const val SESSION_ID = "5598"
        const val RENEWED_SESSION_ID = "151297"
    }
}
