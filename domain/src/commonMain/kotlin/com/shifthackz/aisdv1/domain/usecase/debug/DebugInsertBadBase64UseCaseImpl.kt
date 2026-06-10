package com.shifthackz.aisdv1.domain.usecase.debug

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `DebugInsertBadBase64UseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class DebugInsertBadBase64UseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: GenerationResultRepository,
    /**
     * Exposes the `timeProvider` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val timeProvider: TimeProvider,
) : DebugInsertBadBase64UseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() {
        repository.insert(stubBadBase64Generation(timeProvider.currentTimeMillis()))
    }

    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `stubBadBase64Generation` step in the SDAI domain layer.
         *
         * @param now now value consumed by the API.
         * @author Dmitriy Moroz
         */
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
