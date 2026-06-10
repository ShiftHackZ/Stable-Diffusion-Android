package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GenerationResultLocalDataSource(
    private val dao: GenerationResultDao,
) : GenerationResultDataSource.Local {

    override suspend fun insert(result: AiGenerationResult): Long =
        dao.insert(result.mapDomainToEntity())

    override suspend fun queryAll(): List<AiGenerationResult> =
        dao.query().mapEntityToDomain()

    override suspend fun queryPage(limit: Int, offset: Int): List<AiGenerationResult> =
        dao.queryPage(limit, offset).mapEntityToDomain()

    override fun observePage(limit: Int, offset: Int): Flow<List<AiGenerationResult>> =
        dao.observePage(limit, offset).map { it.mapEntityToDomain() }

    override fun observeCount(): Flow<Int> = dao.observeCount()

    override suspend fun queryById(id: Long): AiGenerationResult =
        dao.queryById(id).mapEntityToDomain()

    override suspend fun queryByIdList(idList: List<Long>): List<AiGenerationResult> =
        dao.queryByIdList(idList).mapEntityToDomain()

    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun deleteByIdList(idList: List<Long>) {
        dao.deleteByIdList(idList)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
