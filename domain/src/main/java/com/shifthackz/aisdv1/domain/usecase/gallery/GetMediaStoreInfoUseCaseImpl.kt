package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

class GetMediaStoreInfoUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetMediaStoreInfoUseCase {

    override fun invoke() = repository.getMediaStoreInfo()
}
