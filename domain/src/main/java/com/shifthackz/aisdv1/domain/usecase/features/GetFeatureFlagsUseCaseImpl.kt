package com.shifthackz.aisdv1.domain.usecase.features

import com.shifthackz.aisdv1.domain.repository.FeatureFlagsRepository

internal class GetFeatureFlagsUseCaseImpl(
    private val featureFlagsRepository: FeatureFlagsRepository,
) : GetFeatureFlagsUseCase {

    override fun invoke() = featureFlagsRepository.get()
}
