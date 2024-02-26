package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionLorasDataSource {

    interface Remote : StableDiffusionLorasDataSource {
        fun fetchLoras(): Single<List<StableDiffusionLora>>
    }

    interface Local : StableDiffusionLorasDataSource {
        fun getLoras(): Single<List<StableDiffusionLora>>

        fun insertLoras(loras: List<StableDiffusionLora>): Completable
    }
}
