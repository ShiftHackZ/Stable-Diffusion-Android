package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import io.reactivex.rxjava3.core.Single

internal class GenerationResultLocalDataSource(
    private val dao: GenerationResultDao,
) : GenerationResultDataSource.Local {

    override fun insert(result: AiGenerationResult) = dao
        .insert(result.mapDomainToEntity())

    override fun insert(results: List<AiGenerationResult>) = dao
        .insert(results.mapDomainToEntity())

    override fun queryAll(): Single<List<AiGenerationResult>> = dao
        .query()
        .map(List<GenerationResultEntity>::mapEntityToDomain)

    override fun queryAllIds(): Single<List<Long>> = dao.queryIds()

    override fun queryPage(limit: Int, offset: Int) = dao
        .queryPage(limit, offset)
        .map(List<GenerationResultEntity>::mapEntityToDomain)

    override fun queryById(id: Long) = dao
        .queryById(id)
        .map(GenerationResultEntity::mapEntityToDomain)

    override fun deleteById(id: Long) = dao.deleteById(id)

    override fun deleteAll() = dao.deleteAll()
}
