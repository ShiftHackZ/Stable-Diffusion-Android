package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import kotlinx.coroutines.flow.Flow

interface ObserveHordeProcessStatusUseCase {
    operator fun invoke(): Flow<HordeProcessStatus>
}
