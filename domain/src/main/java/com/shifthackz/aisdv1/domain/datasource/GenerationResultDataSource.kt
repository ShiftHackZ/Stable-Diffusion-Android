package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface GenerationResultDataSource {

    interface Local : GenerationResultDataSource {
        fun insert(result: AiGenerationResult): Single<Long>
        fun queryAll(): Single<List<AiGenerationResult>>
        fun queryPage(limit: Int, offset: Int): Single<List<AiGenerationResult>>
        fun queryById(id: Long): Single<AiGenerationResult>
        fun queryByIdList(idList: List<Long>): Single<List<AiGenerationResult>>
        fun deleteById(id: Long): Completable
        fun deleteByIdList(idList: List<Long>): Completable
        fun deleteAll(): Completable
    }
}
