package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

sealed interface DownloadableModelDataSource {

    interface Remote : DownloadableModelDataSource {
        fun fetch(): Single<List<LocalAiModel>>
        fun download(id: String, url: String): Observable<DownloadState>
    }

    interface Local : DownloadableModelDataSource {
        fun getAllOnnx(): Single<List<LocalAiModel>>
        fun getAllMediaPipe(): Single<List<LocalAiModel>>
        fun getById(id: String): Single<LocalAiModel>
        fun getSelectedOnnx(): Single<LocalAiModel>
        fun observeAllOnnx(): Flowable<List<LocalAiModel>>
        fun save(list: List<LocalAiModel>): Completable
        fun delete(id: String): Completable
    }
}
