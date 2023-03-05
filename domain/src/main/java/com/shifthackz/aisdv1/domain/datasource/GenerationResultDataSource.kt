package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface GenerationResultDataSource {

    interface Local : GenerationResultDataSource {
        fun insert(result: AiGenerationResultDomain): Completable

        fun getPage(limit: Int, offset: Int): Single<List<AiGenerationResultDomain>>
    }
}
