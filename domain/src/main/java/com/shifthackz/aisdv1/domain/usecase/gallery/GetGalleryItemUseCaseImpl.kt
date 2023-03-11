package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class GetGalleryItemUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetGalleryItemUseCase {

    override operator fun invoke(id: Long) = repository.getById(id)
}
