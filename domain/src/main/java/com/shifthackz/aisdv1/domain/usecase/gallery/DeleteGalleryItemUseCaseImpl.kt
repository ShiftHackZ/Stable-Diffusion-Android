package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Completable

class DeleteGalleryItemUseCaseImpl(
    private val repository: GenerationResultRepository,
) : DeleteGalleryItemUseCase {

    override fun invoke(id: Long): Completable = repository.deleteById(id)
}
