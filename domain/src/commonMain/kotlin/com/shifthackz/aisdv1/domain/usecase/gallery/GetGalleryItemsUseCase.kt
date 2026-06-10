package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

interface GetGalleryItemsUseCase {
    suspend operator fun invoke(ids: List<Long>): List<AiGenerationResult>
}
