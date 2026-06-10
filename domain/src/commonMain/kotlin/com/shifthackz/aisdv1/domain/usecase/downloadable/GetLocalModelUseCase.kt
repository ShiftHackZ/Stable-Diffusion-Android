package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

interface GetLocalModelUseCase {
    suspend operator fun invoke(id: String): LocalAiModel
}
