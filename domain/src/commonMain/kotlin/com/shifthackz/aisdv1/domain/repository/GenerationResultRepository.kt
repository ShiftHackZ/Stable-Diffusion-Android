package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.flow.Flow

interface GenerationResultRepository {

    suspend fun getAll(): List<AiGenerationResult>

    suspend fun getPage(limit: Int, offset: Int): List<AiGenerationResult>

    fun observePage(limit: Int, offset: Int): Flow<List<AiGenerationResult>>

    fun observeCount(): Flow<Int>

    suspend fun getById(id: Long): AiGenerationResult

    suspend fun getByIds(idList: List<Long>): List<AiGenerationResult>

    suspend fun insert(result: AiGenerationResult): Long

    suspend fun deleteById(id: Long)

    suspend fun deleteByIdList(idList: List<Long>)

    suspend fun deleteAll()

    suspend fun toggleVisibility(id: Long): Boolean
}
