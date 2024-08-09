package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface GenerationResultRepository {

    fun getAll(): Single<List<AiGenerationResult>>

    fun getPage(limit: Int, offset: Int): Single<List<AiGenerationResult>>

    fun getMediaStoreInfo(): Single<MediaStoreInfo>

    fun getById(id: Long): Single<AiGenerationResult>

    fun getByIds(idList: List<Long>): Single<List<AiGenerationResult>>

    fun insert(result: AiGenerationResult): Single<Long>

    fun deleteById(id: Long): Completable

    fun deleteByIdList(idList: List<Long>): Completable

    fun deleteAll(): Completable
}
