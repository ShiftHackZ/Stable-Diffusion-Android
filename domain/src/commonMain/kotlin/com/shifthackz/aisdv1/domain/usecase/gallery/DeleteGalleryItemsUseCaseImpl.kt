package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class DeleteGalleryItemsUseCaseImpl(
    private val generationResultRepository: GenerationResultRepository,
) : DeleteGalleryItemsUseCase {

    override suspend fun invoke(ids: List<Long>) {
        generationResultRepository.deleteByIdList(ids)
    }
}
