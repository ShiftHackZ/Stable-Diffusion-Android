package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import io.reactivex.rxjava3.core.Single

interface GetGalleryPageUseCase {
    operator fun invoke(limit: Int, offset: Int): Single<List<AiGenerationResultDomain>>
}
