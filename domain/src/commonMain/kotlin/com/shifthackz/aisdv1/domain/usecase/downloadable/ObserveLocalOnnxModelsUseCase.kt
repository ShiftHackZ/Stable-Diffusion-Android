package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import kotlinx.coroutines.flow.Flow

interface ObserveLocalOnnxModelsUseCase {
    operator fun invoke(): Flow<List<LocalAiModel>>
}
