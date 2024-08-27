package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface DownloadableModelRepository {
    fun download(id: String): Observable<DownloadState>
    fun delete(id: String): Completable
    fun getAllOnnx(): Single<List<LocalAiModel>>
    fun getAllMediaPipe(): Single<List<LocalAiModel>>
    fun observeAllOnnx(): Flowable<List<LocalAiModel>>
}
