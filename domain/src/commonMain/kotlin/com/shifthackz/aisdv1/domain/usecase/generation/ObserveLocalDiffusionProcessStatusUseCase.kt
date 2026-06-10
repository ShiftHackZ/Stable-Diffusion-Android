package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import kotlinx.coroutines.flow.Flow

interface ObserveLocalDiffusionProcessStatusUseCase {
    operator fun invoke(): Flow<LocalDiffusionStatus>
}
