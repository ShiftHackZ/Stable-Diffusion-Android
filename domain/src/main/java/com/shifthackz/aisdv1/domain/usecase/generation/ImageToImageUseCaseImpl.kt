package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

internal class ImageToImageUseCaseImpl(
    private val stableDiffusionGenerationRepository: StableDiffusionGenerationRepository,
    private val swarmUiGenerationRepository: SwarmUiGenerationRepository,
    private val hordeGenerationRepository: HordeGenerationRepository,
    private val huggingFaceGenerationRepository: HuggingFaceGenerationRepository,
    private val stabilityAiGenerationRepository: StabilityAiGenerationRepository,
    private val preferenceManager: PreferenceManager,
) : ImageToImageUseCase {

    override fun invoke(payload: ImageToImagePayload) = Observable
        .range(1, payload.batchCount)
        .flatMapSingle { generate(payload) }
        .toList()

    private fun generate(payload: ImageToImagePayload) = when (preferenceManager.source) {
        ServerSource.AUTOMATIC1111 -> stableDiffusionGenerationRepository.generateFromImage(payload)
        ServerSource.SWARM_UI -> swarmUiGenerationRepository.generateFromImage(payload)
        ServerSource.HORDE -> hordeGenerationRepository.generateFromImage(payload)
        ServerSource.HUGGING_FACE -> huggingFaceGenerationRepository.generateFromImage(payload)
        ServerSource.STABILITY_AI -> stabilityAiGenerationRepository.generateFromImage(payload)
        else -> Single.error(IllegalStateException("Img2Img not yet supported on ${preferenceManager.source}!"))
    }
}
