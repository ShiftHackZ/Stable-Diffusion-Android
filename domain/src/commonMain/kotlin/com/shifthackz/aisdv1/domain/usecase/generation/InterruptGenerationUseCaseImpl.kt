package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository

/**
 * Implements `InterruptGenerationUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class InterruptGenerationUseCaseImpl(
    /**
     * Exposes the `stableDiffusionGenerationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val stableDiffusionGenerationRepository: StableDiffusionGenerationRepository,
    /**
     * Exposes the `hordeGenerationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val hordeGenerationRepository: HordeGenerationRepository,
    /**
     * Exposes the `localDiffusionGenerationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDiffusionGenerationRepository: LocalDiffusionGenerationRepository,
    /**
     * Exposes the `preferenceManager` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : InterruptGenerationUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() {
        when (preferenceManager.source) {
            ServerSource.AUTOMATIC1111 -> stableDiffusionGenerationRepository.interruptGeneration()
            ServerSource.HORDE -> hordeGenerationRepository.interruptGeneration()
            ServerSource.LOCAL_MICROSOFT_ONNX -> localDiffusionGenerationRepository.interruptGeneration()
            else -> Unit
        }
    }
}
