package com.shifthackz.aisdv1.domain.usecase.debug

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Completable
import java.util.Date

class DebugInsertBadBase64UseCaseImpl(
    private val repository: GenerationResultRepository,
) : DebugInsertBadBase64UseCase {

    override fun invoke(): Completable = repository
            .insert(stubBadBase64Generation)
            .ignoreElement()

    companion object {
        private val stubBadBase64Generation = AiGenerationResult(
            0L,
            image = System.currentTimeMillis().toString(),
            inputImage = "",
            createdAt = Date(),
            type = AiGenerationResult.Type.TEXT_TO_IMAGE,
            prompt = "",
            negativePrompt = "",
            width = 512,
            height = 512,
            samplingSteps = 30,
            cfgScale = 0f,
            restoreFaces = false,
            sampler = "",
            seed = "",
            subSeed = "",
            subSeedStrength = 0f,
            denoisingStrength = 0f,
            hidden = false,
        )
    }
}
