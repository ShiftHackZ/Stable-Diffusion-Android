package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

interface GetLocalSdxlModelsUseCase {
    suspend operator fun invoke(): List<LocalAiModel>
}
