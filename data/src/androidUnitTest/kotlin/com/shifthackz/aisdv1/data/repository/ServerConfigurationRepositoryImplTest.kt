package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockServerConfiguration
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ServerConfigurationRepositoryImplTest {

    private val stubBaseUrl = "http://192.168.0.1:7860"
    private val stubCredentials = AuthorizationCredentials.HttpBasic("user", "password")
    private val stubException = RuntimeException("Something went wrong.")
    private val stubRemoteDataSource = mockk<ServerConfigurationDataSource.Remote>()
    private val stubLocalDataSource = mockk<ServerConfigurationDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val repository = ServerConfigurationRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        localDataSource = stubLocalDataSource,
        preferenceManager = stubPreferenceManager,
        authorizationStore = stubAuthorizationStore,
    )

    @Before
    fun initialize() {
        every { stubPreferenceManager.automatic1111ServerUrl } returns stubBaseUrl
        every { stubAuthorizationStore.getAuthorizationCredentials() } returns stubCredentials
    }

    @Test
    fun `given attempt to update configuration, remote completes, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.updateConfiguration(stubBaseUrl, stubCredentials, mockServerConfiguration)
        } returns Unit

        val actual = runCatching { repository.updateConfiguration(mockServerConfiguration) }

        assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to update configuration, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.updateConfiguration(stubBaseUrl, stubCredentials, mockServerConfiguration)
        } throws stubException

        val actual = runCatching { repository.updateConfiguration(mockServerConfiguration) }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to get configuration, local returns data, expected valid domain model value`() = runTest {
        coEvery {
            stubLocalDataSource.get()
        } returns mockServerConfiguration

        assertEquals(mockServerConfiguration, repository.getConfiguration())
    }

    @Test
    fun `given attempt to get configuration, local throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.get()
        } throws stubException

        val actual = runCatching { repository.getConfiguration() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `attempt to fetch configuration, remote returns data, local save success, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchConfiguration(stubBaseUrl, stubCredentials)
        } returns mockServerConfiguration

        coEvery {
            stubLocalDataSource.save(mockServerConfiguration)
        } returns Unit

        val actual = runCatching { repository.fetchConfiguration() }

        assertTrue(actual.isSuccess)
    }

    @Test
    fun `attempt to fetch configuration, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchConfiguration(stubBaseUrl, stubCredentials)
        } throws stubException

        val actual = runCatching { repository.fetchConfiguration() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `attempt to fetch configuration, remote returns data, local save fails, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchConfiguration(stubBaseUrl, stubCredentials)
        } returns mockServerConfiguration

        coEvery {
            stubLocalDataSource.save(mockServerConfiguration)
        } throws stubException

        val actual = runCatching { repository.fetchConfiguration() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to fetch and get, fetch success, get success, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchConfiguration(stubBaseUrl, stubCredentials)
        } returns mockServerConfiguration

        coEvery {
            stubLocalDataSource.save(mockServerConfiguration)
        } returns Unit

        coEvery {
            stubLocalDataSource.get()
        } returns mockServerConfiguration

        assertEquals(mockServerConfiguration, repository.fetchAndGetConfiguration())
    }

    @Test
    fun `given attempt to fetch and get, fetch fails, get success, expected valid domain model value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchConfiguration(stubBaseUrl, stubCredentials)
        } throws stubException

        coEvery {
            stubLocalDataSource.get()
        } returns mockServerConfiguration

        assertEquals(mockServerConfiguration, repository.fetchAndGetConfiguration())
    }

    @Test
    fun `given attempt to fetch and get, fetch fails, get fails, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchConfiguration(stubBaseUrl, stubCredentials)
        } throws stubException

        coEvery {
            stubLocalDataSource.get()
        } throws stubException

        val actual = runCatching { repository.fetchAndGetConfiguration() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
