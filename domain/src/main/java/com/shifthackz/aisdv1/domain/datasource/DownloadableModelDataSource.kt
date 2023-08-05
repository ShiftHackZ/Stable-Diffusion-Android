package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.DownloadState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

sealed interface DownloadableModelDataSource {

    interface Remote : DownloadableModelDataSource {
        fun download(): Observable<DownloadState>
    }

    interface Local : DownloadableModelDataSource {
        fun exists(): Single<Boolean>
    }
}
