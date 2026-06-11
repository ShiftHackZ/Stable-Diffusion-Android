package com.shifthackz.aisdv1.domain.usecase.forgemodule

import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ForgeModulesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetForgeModulesUseCaseImplTest {

    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubRepository = mockk<ForgeModulesRepository>()

    private val useCase = GetForgeModulesUseCaseImpl(
        preferenceManager = stubPreferenceManager,
        repository = stubRepository,
    )

    @Test
    fun `given automatic1111 source and repository returns modules, expected valid modules list value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRepository.fetchModules()
        } returns mockForgeModules

        assertEquals(mockForgeModules, useCase())
    }

    @Test
    fun `given automatic1111 source and repository throws exception, expected empty list`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        coEvery {
            stubRepository.fetchModules()
        } throws RuntimeException("Network error.")

        assertEquals(emptyList<ForgeModule>(), useCase())
    }

    @Test
    fun `given inactive source, expected empty list and no remote fetch`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.SWARM_UI

        assertEquals(emptyList<ForgeModule>(), useCase())

        coVerify(exactly = 0) {
            stubRepository.fetchModules()
        }
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
