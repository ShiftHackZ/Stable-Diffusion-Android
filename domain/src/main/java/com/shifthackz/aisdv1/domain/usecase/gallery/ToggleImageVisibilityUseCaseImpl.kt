package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class ToggleImageVisibilityUseCaseImpl(
    private val repository: GenerationResultRepository,
) : ToggleImageVisibilityUseCase {

    override fun invoke(id: Long) = repository.toggleVisibility(id)
}
