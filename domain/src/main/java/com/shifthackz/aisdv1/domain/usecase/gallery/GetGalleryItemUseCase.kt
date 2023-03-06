package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Single

interface GetGalleryItemUseCase {
    operator fun invoke(id: Long): Single<AiGenerationResult>
}
