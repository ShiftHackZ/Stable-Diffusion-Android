package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.flow.Flow

sealed interface GenerationResultDataSource {

    interface Local : GenerationResultDataSource {
        suspend fun insert(result: AiGenerationResult): Long
        suspend fun queryAll(): List<AiGenerationResult>
        suspend fun queryPage(limit: Int, offset: Int): List<AiGenerationResult>
        fun observePage(limit: Int, offset: Int): Flow<List<AiGenerationResult>>
        fun observeCount(): Flow<Int>
        suspend fun queryById(id: Long): AiGenerationResult
        suspend fun queryByIdList(idList: List<Long>): List<AiGenerationResult>
        suspend fun deleteById(id: Long)
        suspend fun deleteByIdList(idList: List<Long>)
        suspend fun deleteAll()
    }
}
