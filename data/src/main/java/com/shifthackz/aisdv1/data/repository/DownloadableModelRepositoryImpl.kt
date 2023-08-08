package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Completable

internal class DownloadableModelRepositoryImpl(
    private val remoteDataSource: DownloadableModelDataSource.Remote,
    private val localDataSource: DownloadableModelDataSource.Local,
) : DownloadableModelRepository {

    override fun isModelDownloaded() = localDataSource.exists()

    override fun download() = remoteDataSource.download()

    override fun delete() = localDataSource.delete()
}
