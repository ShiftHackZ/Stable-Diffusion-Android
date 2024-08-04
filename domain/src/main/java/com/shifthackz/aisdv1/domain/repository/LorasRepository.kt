package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.LoRA
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface LorasRepository {
    fun fetchLoras(): Completable
    fun fetchAndGetLoras(): Single<List<LoRA>>
    fun getLoras(): Single<List<LoRA>>
}
