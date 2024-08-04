package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single

class GetMediaStoreInfoUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetMediaStoreInfoUseCase {

    override fun invoke(): Single<MediaStoreInfo> = repository.getMediaStoreInfo()
}
