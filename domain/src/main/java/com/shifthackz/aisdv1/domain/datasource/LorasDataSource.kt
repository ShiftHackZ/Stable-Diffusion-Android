package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.LoRA
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface LorasDataSource {

    sealed interface Remote : LorasDataSource {

        interface Automatic1111 : Remote {
            fun fetchLoras(): Single<List<LoRA>>
        }

        interface SwarmUi : Remote {
            fun fetchLoras(sessionId: String): Single<List<LoRA>>
        }
    }

    interface Local : LorasDataSource {
        fun getLoras(): Single<List<LoRA>>
        fun insertLoras(loras: List<LoRA>): Completable
    }
}
