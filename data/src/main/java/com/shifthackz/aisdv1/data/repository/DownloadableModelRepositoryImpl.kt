package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class DownloadableModelRepositoryImpl(
    private val remoteDataSource: DownloadableModelDataSource.Remote,
    private val localDataSource: DownloadableModelDataSource.Local,
) : DownloadableModelRepository {

    override fun isModelDownloaded(id: String) = localDataSource.isDownloaded(id)

    override fun download(id: String) = localDataSource
        .getById(id)
        .flatMapObservable { model ->
            remoteDataSource.download(id, model.sources.firstOrNull() ?: "")
        }

    override fun delete(id: String) = localDataSource.delete(id)

    override fun getAll() = remoteDataSource
        .fetch()
        .flatMapCompletable(localDataSource::save)
        .andThen(localDataSource.getAll())
        .onErrorResumeNext { localDataSource.getAll() }

    override fun getById(id: String) = localDataSource.getById(id)

    override fun observeAll() = localDataSource.observeAll()

    override fun select(id: String) = localDataSource.select(id)
}
