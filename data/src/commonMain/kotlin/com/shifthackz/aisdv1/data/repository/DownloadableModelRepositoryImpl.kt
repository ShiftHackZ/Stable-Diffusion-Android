package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

internal class DownloadableModelRepositoryImpl(
    private val remoteDataSource: DownloadableModelDataSource.Remote,
    private val localDataSource: DownloadableModelDataSource.Local,
    private val buildInfoProvider: BuildInfoProvider,
) : DownloadableModelRepository {

    override fun download(id: String, url: String) = remoteDataSource.download(id, url)

    override suspend fun delete(id: String) = localDataSource.delete(id)

    override suspend fun getAllOnnx(): List<LocalAiModel> {
        refreshCache()
        return localDataSource.getAllOnnx()
    }

    override suspend fun getAllMediaPipe(): List<LocalAiModel> {
        if (buildInfoProvider.type == BuildType.FOSS) {
            return emptyList()
        }
        refreshCache()
        return localDataSource.getAllMediaPipe()
    }

    override fun observeAllOnnx() = localDataSource.observeAllOnnx()

    private suspend fun refreshCache() {
        runCatching {
            localDataSource.save(remoteDataSource.fetch())
        }
    }
}
