package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class GenerationResultLocalDataSource(
    private val dao: GenerationResultDao,
) : GenerationResultDataSource.Local {

    override fun insert(result: AiGenerationResultDomain): Completable = dao
        .insert(result.mapDomainToEntity())


    override fun queryAll(): Single<List<AiGenerationResultDomain>> = dao
        .query()
        .map(List<GenerationResultEntity>::mapEntityToDomain)

    override fun queryPage(limit: Int, offset: Int): Single<List<AiGenerationResultDomain>> = dao
        .queryPage(limit, offset)
        .map(List<GenerationResultEntity>::mapEntityToDomain)
}
