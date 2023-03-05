package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

class GetGalleryPageUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetGalleryPageUseCase {

    override operator fun invoke(limit: Int, offset: Int) = repository.getPage(limit, offset)
        .map {
            println("PAGED_UC -> limit=$limit, offset=$offset, payload=${it.size}")
            it
        }
}
