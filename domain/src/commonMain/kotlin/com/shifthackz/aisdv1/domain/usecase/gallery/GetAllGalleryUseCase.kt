package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

interface GetAllGalleryUseCase {
    suspend operator fun invoke(): List<AiGenerationResult>
}
