package com.shifthackz.aisdv1.domain.usecase.sdsampler

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository

/**
 * Implements `GetStableDiffusionSamplersUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class GetStableDiffusionSamplersUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: StableDiffusionSamplersRepository,
) : GetStableDiffusionSamplersUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke(): List<StableDiffusionSampler> {
        runCatching { repository.fetchSamplers() }
        return repository.getSamplers()
    }
}
