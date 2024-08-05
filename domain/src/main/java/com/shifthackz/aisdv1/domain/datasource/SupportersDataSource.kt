package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Supporter
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface SupportersDataSource {

    interface Remote : SupportersDataSource {
        fun fetch(): Single<List<Supporter>>
    }

    interface Local : SupportersDataSource {
        fun save(data: List<Supporter>): Completable
        fun getAll(): Single<List<Supporter>>
    }
}
