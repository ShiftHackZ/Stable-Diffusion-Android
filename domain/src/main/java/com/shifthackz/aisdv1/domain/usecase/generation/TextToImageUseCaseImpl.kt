package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class TextToImageUseCaseImpl(
    private val stableDiffusionGenerationRepository: StableDiffusionGenerationRepository,
    private val hordeGenerationRepository: HordeGenerationRepository,
    private val localDiffusionGenerationRepository: LocalDiffusionGenerationRepository,
    private val preferenceManager: PreferenceManager,
) : TextToImageUseCase {

    override operator fun invoke(payload: TextToImagePayload) = execute(payload)

    private fun execute(payload: TextToImagePayload): Single<AiGenerationResult> {
        return when (preferenceManager.source) {
            ServerSource.HORDE -> hordeGenerationRepository.generateFromText(payload)
            ServerSource.LOCAL -> localDiffusionGenerationRepository.generateFromText(payload)
            else -> stableDiffusionGenerationRepository.generateFromText(payload)
        }
    }
}
