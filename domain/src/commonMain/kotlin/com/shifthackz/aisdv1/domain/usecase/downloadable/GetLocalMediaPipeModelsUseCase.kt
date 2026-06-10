package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

interface GetLocalMediaPipeModelsUseCase {
    suspend operator fun invoke(): List<LocalAiModel>
}
