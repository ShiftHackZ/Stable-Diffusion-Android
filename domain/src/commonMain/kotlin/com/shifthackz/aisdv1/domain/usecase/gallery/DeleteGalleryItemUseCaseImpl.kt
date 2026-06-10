package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class DeleteGalleryItemUseCaseImpl(
    private val repository: GenerationResultRepository,
) : DeleteGalleryItemUseCase {

    override suspend fun invoke(id: Long) {
        repository.deleteById(id)
    }
}
