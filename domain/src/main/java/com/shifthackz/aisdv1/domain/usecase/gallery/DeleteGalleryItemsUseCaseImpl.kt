package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Completable

internal class DeleteGalleryItemsUseCaseImpl(
    private val generationResultRepository: GenerationResultRepository,
) : DeleteGalleryItemsUseCase {

    override fun invoke(ids: List<Long>): Completable = generationResultRepository.deleteByIdList(ids)
}
