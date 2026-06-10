package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine

interface FetchAndGetStabilityAiEnginesUseCase {

    suspend operator fun invoke(): List<StabilityAiEngine>
}
