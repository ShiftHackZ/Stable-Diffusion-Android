package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class DeleteAllGalleryUseCaseImpl(
    private val generationResultRepository: GenerationResultRepository,
) : DeleteAllGalleryUseCase {

    override suspend fun invoke() {
        generationResultRepository.deleteAll()
    }
}
