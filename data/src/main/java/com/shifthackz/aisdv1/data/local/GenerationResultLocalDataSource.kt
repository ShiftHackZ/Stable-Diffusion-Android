package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import io.reactivex.rxjava3.core.Completable

class GenerationResultLocalDataSource(
    private val dao: GenerationResultDao,
) : GenerationResultDataSource.Local {

    override fun insert(result: AiGenerationResultDomain): Completable = dao
        .insert(result.mapDomainToEntity())
}
