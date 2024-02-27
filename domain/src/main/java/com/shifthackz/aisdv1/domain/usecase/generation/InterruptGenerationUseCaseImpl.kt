package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository

internal class InterruptGenerationUseCaseImpl(
    private val stableDiffusionGenerationRepository: StableDiffusionGenerationRepository,
    private val hordeGenerationRepository: HordeGenerationRepository,
    private val localDiffusionGenerationRepository: LocalDiffusionGenerationRepository,
    private val preferenceManager: PreferenceManager,
) : InterruptGenerationUseCase {

    override fun invoke() = when (preferenceManager.source) {
        ServerSource.CUSTOM -> stableDiffusionGenerationRepository.interruptGeneration()
        ServerSource.HORDE -> hordeGenerationRepository.interruptGeneration()
        ServerSource.LOCAL -> localDiffusionGenerationRepository.interruptGeneration()
    }
}
