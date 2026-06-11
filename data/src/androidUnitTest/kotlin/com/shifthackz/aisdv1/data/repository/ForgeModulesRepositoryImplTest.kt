package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ForgeModulesDataSource
import com.shifthackz.aisdv1.domain.entity.ForgeModule
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

class ForgeModulesRepositoryImplTest {

    private val stubBaseUrl = "http://192.168.0.1:7860"
    private val stubCredentials = AuthorizationCredentials.HttpBasic("user", "password")
    private val stubException = RuntimeException("Something went wrong.")
    private val stubRemoteDataSource = mockk<ForgeModulesDataSource>()
    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val repository = ForgeModulesRepositoryImpl(
        remoteDataSource = stubRemoteDataSource,
        preferenceManager = stubPreferenceManager,
        authorizationStore = stubAuthorizationStore,
    )

    @Before
    fun initialize() {
        every { stubPreferenceManager.automatic1111ServerUrl } returns stubBaseUrl
        every { stubAuthorizationStore.getAuthorizationCredentials() } returns stubCredentials
    }

    @Test
    fun `given attempt to fetch forge modules, remote returns data, expected valid modules list value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchModules(stubBaseUrl, stubCredentials)
        } returns mockForgeModules

        assertEquals(mockForgeModules, repository.fetchModules())
    }

    @Test
    fun `given attempt to fetch forge modules, remote throws exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.fetchModules(stubBaseUrl, stubCredentials)
        } throws stubException

        val actual = runCatching { repository.fetchModules() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }

    private companion object {
        val mockForgeModules = listOf(
            ForgeModule(
                name = "sdxl_vae",
                path = "/models/VAE/sdxl_vae.safetensors",
            ),
        )
    }
}
