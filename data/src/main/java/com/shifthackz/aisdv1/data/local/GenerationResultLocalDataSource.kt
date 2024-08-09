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

    override fun insert(result: AiGenerationResult) = result
        .mapDomainToEntity()
        .let(dao::insert)

    override fun queryAll(): Single<List<AiGenerationResult>> = dao
        .query()
        .map(List<GenerationResultEntity>::mapEntityToDomain)

    override fun queryPage(limit: Int, offset: Int) = dao
        .queryPage(limit, offset)
        .map(List<GenerationResultEntity>::mapEntityToDomain)

    override fun queryById(id: Long) = dao
        .queryById(id)
        .map(GenerationResultEntity::mapEntityToDomain)

    override fun queryByIdList(idList: List<Long>) = dao
        .queryByIdList(idList)
        .map(List<GenerationResultEntity>::mapEntityToDomain)

    override fun deleteById(id: Long) = dao.deleteById(id)

    override fun deleteByIdList(idList: List<Long>) = dao.deleteByIdList(idList)

    override fun deleteAll() = dao.deleteAll()
}
