package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.flow.Flow

interface GetGenerationResultPagedUseCase {
    suspend operator fun invoke(limit: Int, offset: Int): List<AiGenerationResult>
    fun observe(limit: Int, offset: Int): Flow<List<AiGenerationResult>>
    fun observeCount(): Flow<Int>
}
