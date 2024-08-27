package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class DownloadableModelRepositoryImpl(
    private val remoteDataSource: DownloadableModelDataSource.Remote,
    private val localDataSource: DownloadableModelDataSource.Local,
) : DownloadableModelRepository {

    override fun download(id: String) = localDataSource
        .getById(id)
        .flatMapObservable { model ->
            remoteDataSource.download(id, model.sources.firstOrNull() ?: "")
        }

    override fun delete(id: String) = localDataSource.delete(id)

    override fun getAllOnnx() = remoteDataSource
        .fetch()
        .flatMapCompletable(localDataSource::save)
        .andThen(localDataSource.getAllOnnx())
        .onErrorResumeNext { localDataSource.getAllOnnx() }

    override fun getAllMediaPipe() = remoteDataSource
        .fetch()
        .flatMapCompletable(localDataSource::save)
        .andThen(localDataSource.getAllMediaPipe())
        .onErrorResumeNext { localDataSource.getAllMediaPipe() }

    override fun observeAllOnnx() = localDataSource.observeAllOnnx()
}
