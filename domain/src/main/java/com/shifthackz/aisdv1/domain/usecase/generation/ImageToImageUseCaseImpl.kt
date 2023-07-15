package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.CoinRepository
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class ImageToImageUseCaseImpl(
    private val stableDiffusionGenerationRepository: StableDiffusionGenerationRepository,
    private val hordeGenerationRepository: HordeGenerationRepository,
    private val coinRepository: CoinRepository,
    private val preferenceManager: PreferenceManager,
) : ImageToImageUseCase {

    override fun invoke(payload: ImageToImagePayload) = execute(payload)
        .flatMap { result ->
            coinRepository
                .spendCoin()
                .andThen(Single.just(result))
        }

    private fun execute(payload: ImageToImagePayload): Single<AiGenerationResult> {
        if (preferenceManager.source == ServerSource.HORDE) {
            return hordeGenerationRepository.generateFromImage(payload)
        }
        return stableDiffusionGenerationRepository.generateFromImage(payload)
    }
}
