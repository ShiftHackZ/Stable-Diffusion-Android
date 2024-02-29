package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.lang.IllegalStateException

internal class ImageToImageUseCaseImpl(
    private val stableDiffusionGenerationRepository: StableDiffusionGenerationRepository,
    private val hordeGenerationRepository: HordeGenerationRepository,
    private val huggingFaceGenerationRepository: HuggingFaceGenerationRepository,
    private val preferenceManager: PreferenceManager,
) : ImageToImageUseCase {

    override fun invoke(payload: ImageToImagePayload) = Observable
        .range(1, payload.batchCount)
        .flatMapSingle { generate(payload) }
        .toList()

    private fun generate(payload: ImageToImagePayload) = when (preferenceManager.source) {
        ServerSource.AUTOMATIC1111 -> stableDiffusionGenerationRepository.generateFromImage(payload)
        ServerSource.HORDE -> hordeGenerationRepository.generateFromImage(payload)
        ServerSource.HUGGING_FACE -> huggingFaceGenerationRepository.generateFromImage(payload)
        ServerSource.LOCAL -> Single.error(IllegalStateException("Img2Img not yet supported on Local Diffusion!"))
    }
}
