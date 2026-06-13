package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import kotlinx.coroutines.flow.Flow

interface ObserveLocalSdxlModelsUseCase {
    operator fun invoke(): Flow<List<LocalAiModel>>
}
