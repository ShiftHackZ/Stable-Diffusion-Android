package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
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

class StableDiffusionModelsRepositoryImplTest {

    private val stubBaseUrl = "http://192.168.0.1:7860"
    private val stubCredentials = AuthorizationCredentials.HttpBasic("user", "password")
    private val stubException = RuntimeException("Something went wrong.")
    private val stubRemoteDataSource = mockk<StableDiffusionModelsDataSource.Remote>()
    private val stubLocalDataSource = mockk<StableDiffusionModelsDataSource.Local>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val repository = StableDiffusionModelsRepositoryImpl(
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
    fun `given attempt to fetch models, remote returns data, local insert success, expected complete value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials)
        } returns mockStableDiffusionModels

        coEvery {
            stubLocalDataSource.insertModels(mockStableDiffusionModels)
        } returns Unit

        val actual = runCatching { repository.fetchModels() }

        assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to fetch models, remote throws exception, local insert success, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials)
        } throws stubException

        val actual = runCatching { repository.fetchModels() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to fetch models, remote returns data, local insert fails, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials)
        } returns mockStableDiffusionModels

        coEvery {
            stubLocalDataSource.insertModels(mockStableDiffusionModels)
        } throws stubException

        val actual = runCatching { repository.fetchModels() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to get models, local data source returns list, expected valid domain models list value`() = runTest {
        coEvery {
            stubLocalDataSource.getModels()
        } returns mockStableDiffusionModels

        assertEquals(mockStableDiffusionModels, repository.getModels())
    }

    @Test
    fun `given attempt to get models, local data source returns empty list, expected empty domain models list value`() = runTest {
        coEvery {
            stubLocalDataSource.getModels()
        } returns emptyList()

        assertEquals(emptyList<Any>(), repository.getModels())
    }

    @Test
    fun `given attempt to get models, local data source throws exception, expected error value`() = runTest {
        coEvery {
            stubLocalDataSource.getModels()
        } throws stubException

        val actual = runCatching { repository.getModels() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    @Test
    fun `given attempt to fetch and get models, remote returns data, local returns data, expected valid domain models list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials)
        } returns mockStableDiffusionModels

        coEvery {
            stubLocalDataSource.insertModels(mockStableDiffusionModels)
        } returns Unit

        coEvery {
            stubLocalDataSource.getModels()
        } returns mockStableDiffusionModels

        assertEquals(mockStableDiffusionModels, repository.fetchAndGetModels())
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local returns data, expected valid domain models list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials)
        } throws stubException

        coEvery {
            stubLocalDataSource.getModels()
        } returns mockStableDiffusionModels

        assertEquals(mockStableDiffusionModels, repository.fetchAndGetModels())
    }

    @Test
    fun `given attempt to fetch and get models, remote fails, local fails, expected valid error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchSdModels(stubBaseUrl, stubCredentials)
        } throws stubException

        coEvery {
            stubLocalDataSource.getModels()
        } throws stubException

        val actual = runCatching { repository.fetchAndGetModels() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
