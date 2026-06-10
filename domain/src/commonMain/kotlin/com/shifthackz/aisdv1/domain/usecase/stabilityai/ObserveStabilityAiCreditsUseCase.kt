package com.shifthackz.aisdv1.domain.usecase.stabilityai

import kotlinx.coroutines.flow.Flow

interface ObserveStabilityAiCreditsUseCase {
    operator fun invoke(): Flow<Float>
}
