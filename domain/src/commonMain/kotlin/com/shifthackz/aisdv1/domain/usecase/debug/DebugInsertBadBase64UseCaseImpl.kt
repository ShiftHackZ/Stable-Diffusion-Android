package com.shifthackz.aisdv1.domain.usecase.debug

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

class DebugInsertBadBase64UseCaseImpl(
    private val repository: GenerationResultRepository,
    private val timeProvider: TimeProvider,
) : DebugInsertBadBase64UseCase {

    override suspend fun invoke() {
        repository.insert(stubBadBase64Generation(timeProvider.currentTimeMillis()))
    }

    companion object {
        private fun stubBadBase64Generation(now: Long) = AiGenerationResult(
            0L,
            image = now.toString(),
            inputImage = "",
            createdAt = now,
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
