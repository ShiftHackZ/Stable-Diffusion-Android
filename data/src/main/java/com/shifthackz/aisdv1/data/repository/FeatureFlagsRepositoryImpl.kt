package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.domain.datasource.FeatureFlagsDataSource
import com.shifthackz.aisdv1.domain.repository.FeatureFlagsRepository

internal class FeatureFlagsRepositoryImpl(
    private val buildInfoProvider: BuildInfoProvider,
    private val remoteDataSource: FeatureFlagsDataSource.Remote,
    private val localDataSource: FeatureFlagsDataSource.Local,
) : FeatureFlagsRepository {

    override fun get() = when (buildInfoProvider.buildType) {
        BuildType.FOSS -> localDataSource.get()
        BuildType.GOOGLE_PLAY -> localDataSource
            .getIsLoaded()
            .flatMap { loaded ->
                if (loaded) {
                    return@flatMap localDataSource.get()
                }
                return@flatMap remoteDataSource.fetch()
                    .flatMapCompletable(localDataSource::store)
                    .andThen(localDataSource.get())
            }
    }
}
