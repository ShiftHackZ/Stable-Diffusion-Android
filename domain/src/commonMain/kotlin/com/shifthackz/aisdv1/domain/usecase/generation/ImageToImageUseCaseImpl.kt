package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository

internal class ImageToImageUseCaseImpl(
    private val stableDiffusionGenerationRepository: StableDiffusionGenerationRepository,
    private val swarmUiGenerationRepository: SwarmUiGenerationRepository,
    private val hordeGenerationRepository: HordeGenerationRepository,
    private val huggingFaceGenerationRepository: HuggingFaceGenerationRepository,
    private val stabilityAiGenerationRepository: StabilityAiGenerationRepository,
    private val preferenceManager: PreferenceManager,
) : ImageToImageUseCase {

    override suspend fun invoke(payload: ImageToImagePayload) = when (preferenceManager.source) {
        ServerSource.AUTOMATIC1111 -> stableDiffusionGenerationRepository.generateFromImage(payload)
        else -> List(payload.batchCount.coerceAtLeast(1)) {
            generateSingle(payload)
        }
    }

    private suspend fun generateSingle(payload: ImageToImagePayload) = when (preferenceManager.source) {
        ServerSource.SWARM_UI -> swarmUiGenerationRepository.generateFromImage(payload)
        ServerSource.HORDE -> hordeGenerationRepository.generateFromImage(payload)
        ServerSource.HUGGING_FACE -> huggingFaceGenerationRepository.generateFromImage(payload)
        ServerSource.STABILITY_AI -> stabilityAiGenerationRepository.generateFromImage(payload)
        ServerSource.AUTOMATIC1111 -> error("Automatic1111 batch must be generated through generateFromImage(payload).")
        else -> throw IllegalStateException("Img2Img not yet supported on ${preferenceManager.source}!")
    }
}
