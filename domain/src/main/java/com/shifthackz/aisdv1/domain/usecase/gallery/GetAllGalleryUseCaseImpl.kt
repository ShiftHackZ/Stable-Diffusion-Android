package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

class GetAllGalleryUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetAllGalleryUseCase {

    override operator fun invoke() = repository.getAll()
}
