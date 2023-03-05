package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import io.reactivex.rxjava3.core.Single

interface GenerationResultRepository {

    fun getAll(): Single<List<AiGenerationResultDomain>>

    fun getPage(limit: Int, offset: Int): Single<List<AiGenerationResultDomain>>

    fun getById(id: Long): Single<AiGenerationResultDomain>
}
