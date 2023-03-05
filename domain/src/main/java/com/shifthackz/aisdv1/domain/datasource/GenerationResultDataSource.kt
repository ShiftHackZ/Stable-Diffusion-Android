package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import io.reactivex.rxjava3.core.Single

sealed interface GenerationResultDataSource {

    interface Local : GenerationResultDataSource {
        fun insert(result: AiGenerationResultDomain): Single<Long>
        fun queryAll(): Single<List<AiGenerationResultDomain>>
        fun queryPage(limit: Int, offset: Int): Single<List<AiGenerationResultDomain>>
        fun queryById(id: Long): Single<AiGenerationResultDomain>
    }
}
