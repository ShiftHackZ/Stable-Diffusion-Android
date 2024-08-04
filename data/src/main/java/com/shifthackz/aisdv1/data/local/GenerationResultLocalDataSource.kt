package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class GenerationResultLocalDataSource(
    private val dao: GenerationResultDao,
) : GenerationResultDataSource.Local {

    override fun insert(result: AiGenerationResult): Single<Long> = result
        .mapDomainToEntity()
        .let(dao::insert)

    override fun queryAll(): Single<List<AiGenerationResult>> = dao
        .query()
        .map(List<GenerationResultEntity>::mapEntityToDomain)

    override fun queryPage(limit: Int, offset: Int): Single<List<AiGenerationResult>> = dao
        .queryPage(limit, offset)
        .map(List<GenerationResultEntity>::mapEntityToDomain)

    override fun queryById(id: Long): Single<AiGenerationResult> = dao
        .queryById(id)
        .map(GenerationResultEntity::mapEntityToDomain)

    override fun deleteById(id: Long): Completable = dao.deleteById(id)

    override fun deleteAll(): Completable = dao.deleteAll()
}
