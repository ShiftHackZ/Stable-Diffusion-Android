package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.MediaPipeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.OpenAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

internal class TextToImageUseCaseImpl(
    private val stableDiffusionGenerationRepository: StableDiffusionGenerationRepository,
    private val hordeGenerationRepository: HordeGenerationRepository,
    private val huggingFaceGenerationRepository: HuggingFaceGenerationRepository,
    private val openAiGenerationRepository: OpenAiGenerationRepository,
    private val stabilityAiGenerationRepository: StabilityAiGenerationRepository,
    private val swarmUiGenerationRepository: SwarmUiGenerationRepository,
    private val localDiffusionGenerationRepository: LocalDiffusionGenerationRepository,
    private val mediaPipeGenerationRepository: MediaPipeGenerationRepository,
    private val preferenceManager: PreferenceManager,
) : TextToImageUseCase {

    override operator fun invoke(
        payload: TextToImagePayload,
    ): Single<List<AiGenerationResult>> = Observable
        .range(1, payload.batchCount)
        .flatMapSingle { generate(payload) }
        .toList()

    private fun generate(payload: TextToImagePayload) = when (preferenceManager.source) {
        ServerSource.HORDE -> hordeGenerationRepository.generateFromText(payload)
        ServerSource.LOCAL_MICROSOFT_ONNX -> localDiffusionGenerationRepository.generateFromText(payload)
        ServerSource.HUGGING_FACE -> huggingFaceGenerationRepository.generateFromText(payload)
        ServerSource.AUTOMATIC1111 -> stableDiffusionGenerationRepository.generateFromText(payload)
        ServerSource.OPEN_AI -> openAiGenerationRepository.generateFromText(payload)
        ServerSource.STABILITY_AI -> stabilityAiGenerationRepository.generateFromText(payload)
        ServerSource.SWARM_UI -> swarmUiGenerationRepository.generateFromText(payload)
        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> mediaPipeGenerationRepository.generateFromText(payload)
    }
}
