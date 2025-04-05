package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.Single

internal class DownloadableModelRepositoryImpl(
    private val remoteDataSource: DownloadableModelDataSource.Remote,
    private val localDataSource: DownloadableModelDataSource.Local,
    private val buildInfoProvider: BuildInfoProvider,
) : DownloadableModelRepository {

    override fun download(id: String, url: String) = remoteDataSource.download(id, url)

    override fun delete(id: String) = localDataSource.delete(id)

    override fun getAllOnnx() = remoteDataSource
        .fetch()
        .flatMapCompletable(localDataSource::save)
        .andThen(localDataSource.getAllOnnx())
        .onErrorResumeNext { localDataSource.getAllOnnx() }

    override fun getAllMediaPipe(): Single<List<LocalAiModel>> {
        if (buildInfoProvider.type == BuildType.FOSS) {
            return Single.just(emptyList())
        }
        return remoteDataSource
            .fetch()
            .flatMapCompletable(localDataSource::save)
            .andThen(localDataSource.getAllMediaPipe())
            .onErrorResumeNext { localDataSource.getAllMediaPipe() }
    }

    override fun observeAllOnnx() = localDataSource.observeAllOnnx()
}
