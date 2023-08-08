package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.DownloadState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface DownloadableModelRepository {
    fun isModelDownloaded(): Single<Boolean>
    fun download(): Observable<DownloadState>
    fun delete(): Completable
}
