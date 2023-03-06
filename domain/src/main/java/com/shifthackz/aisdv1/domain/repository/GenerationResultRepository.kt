package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface GenerationResultRepository {

    fun getAll(): Single<List<AiGenerationResult>>

    fun getPage(limit: Int, offset: Int): Single<List<AiGenerationResult>>

    fun getById(id: Long): Single<AiGenerationResult>

    fun deleteById(id: Long): Completable
}
