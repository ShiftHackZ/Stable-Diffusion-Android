package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Single

interface GetAllGalleryUseCase {
    operator fun invoke(): Single<List<AiGenerationResult>>
}
