package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import io.reactivex.rxjava3.core.Completable

sealed interface GenerationResultDataSource {

    interface Local : GenerationResultDataSource {
        fun insert(result: AiGenerationResultDomain): Completable
    }
}
