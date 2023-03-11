package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class GetGalleryPageUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetGalleryPageUseCase {

    override operator fun invoke(limit: Int, offset: Int) = repository.getPage(limit, offset)
}
