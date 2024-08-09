package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class GetGalleryItemsUseCaseImpl(
    private val generationResultRepository: GenerationResultRepository,
) : GetGalleryItemsUseCase {

    override fun invoke(ids: List<Long>) = generationResultRepository.getByIds(ids)
}
