package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreMediaStoreRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import kotlinx.coroutines.flow.Flow

internal class GenerationResultRepositoryImpl(
    preferenceManager: PreferenceManager,
    mediaStoreGateway: MediaStoreGateway,
    private val localDataSource: GenerationResultDataSource.Local,
) : CoreMediaStoreRepository(
    preferenceManager,
    mediaStoreGateway,
), GenerationResultRepository {

    override suspend fun getAll(): List<AiGenerationResult> = localDataSource.queryAll()

    override suspend fun getPage(limit: Int, offset: Int): List<AiGenerationResult> =
        localDataSource.queryPage(limit, offset)

    override fun observePage(limit: Int, offset: Int): Flow<List<AiGenerationResult>> =
        localDataSource.observePage(limit, offset)

    override fun observeCount(): Flow<Int> = localDataSource.observeCount()

    override suspend fun getById(id: Long): AiGenerationResult = localDataSource.queryById(id)

    override suspend fun getByIds(idList: List<Long>): List<AiGenerationResult> =
        localDataSource.queryByIdList(idList)

    override suspend fun insert(result: AiGenerationResult): Long {
        val id = localDataSource.insert(result)
        exportToMediaStoreAsync(result)
        return id
    }

    override suspend fun deleteById(id: Long) {
        localDataSource.deleteById(id)
    }

    override suspend fun deleteByIdList(idList: List<Long>) {
        localDataSource.deleteByIdList(idList)
    }

    override suspend fun deleteAll() {
        localDataSource.deleteAll()
    }

    override suspend fun toggleVisibility(id: Long): Boolean {
        val updated = localDataSource.queryById(id).let { it.copy(hidden = !it.hidden) }
        localDataSource.insert(updated)
        return localDataSource.queryById(id).hidden
    }
}
