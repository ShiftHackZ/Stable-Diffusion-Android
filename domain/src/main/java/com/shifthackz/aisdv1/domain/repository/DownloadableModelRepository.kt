package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface DownloadableModelRepository {
    fun isModelDownloaded(id: String): Single<Boolean>
    fun download(id: String): Observable<DownloadState>
    fun delete(id: String): Completable
    fun getAll(): Single<List<LocalAiModel>>
    fun getById(id: String): Single<LocalAiModel>
    fun select(id: String): Completable
}
