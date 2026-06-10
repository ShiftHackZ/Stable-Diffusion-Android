package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class GetAllGalleryUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetAllGalleryUseCase {

    override suspend operator fun invoke() = repository.getAll()
}
