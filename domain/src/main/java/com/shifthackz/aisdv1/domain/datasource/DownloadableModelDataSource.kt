package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

sealed interface DownloadableModelDataSource {

    interface Remote : DownloadableModelDataSource {
        fun fetch(): Single<List<LocalAiModel>>
        fun download(id: String, url: String): Observable<DownloadState>
    }

    interface Local : DownloadableModelDataSource {
        fun getAll(): Single<List<LocalAiModel>>
        fun getById(id: String): Single<LocalAiModel>
        fun getSelected(): Single<LocalAiModel>
        fun select(id: String): Completable
        fun save(list: List<LocalAiModel>): Completable
        fun isDownloaded(id: String): Single<Boolean>
        fun delete(id: String): Completable
    }
}
