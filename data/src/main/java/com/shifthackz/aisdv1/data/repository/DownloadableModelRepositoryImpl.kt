package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

internal class DownloadableModelRepositoryImpl(
    private val remoteDataSource: DownloadableModelDataSource.Remote,
    private val localDataSource: DownloadableModelDataSource.Local,
) : DownloadableModelRepository {

    override fun isModelDownloaded(id: String): Single<Boolean> = localDataSource.isDownloaded(id)

    override fun download(id: String): Observable<DownloadState> = localDataSource
        .getById(id)
        .flatMapObservable { model ->
            remoteDataSource.download(id, model.sources.firstOrNull() ?: "")
        }

    override fun delete(id: String): Completable = localDataSource.delete(id)

    override fun getAll(): Single<List<LocalAiModel>> = remoteDataSource
        .fetch()
        .flatMapCompletable(localDataSource::save)
        .andThen(localDataSource.getAll())
        .onErrorResumeNext { localDataSource.getAll() }

    override fun getById(id: String): Single<LocalAiModel> = localDataSource.getById(id)

    override fun observeAll(): Flowable<List<LocalAiModel>> = localDataSource.observeAll()

    override fun select(id: String): Completable = localDataSource.select(id)
}
