package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository

/**
 * Implements `DownloadableModelRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class DownloadableModelRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: DownloadableModelDataSource.Remote,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: DownloadableModelDataSource.Local,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider,
) : DownloadableModelRepository {

    /**
     * Executes the `download` step in the SDAI data layer.
     *
     * @param id identifier of the target entity.
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    override fun download(id: String, url: String) = remoteDataSource.download(id, url)

    /**
     * Performs the SDAI side effect handled by `delete`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override suspend fun delete(id: String) = localDataSource.delete(id)

    /**
     * Loads SDAI data through `getAllOnnx`.
     *
     * @return Result produced by `getAllOnnx`.
     * @author Dmitriy Moroz
     */
    override suspend fun getAllOnnx(): List<LocalAiModel> {
        refreshCache()
        return localDataSource.getAllOnnx()
    }

    /**
     * Loads SDAI data through `getAllMediaPipe`.
     *
     * @return Result produced by `getAllMediaPipe`.
     * @author Dmitriy Moroz
     */
    override suspend fun getAllMediaPipe(): List<LocalAiModel> {
        if (buildInfoProvider.type == BuildType.FOSS) {
            return emptyList()
        }
        refreshCache()
        return localDataSource.getAllMediaPipe()
    }

    /**
     * Loads SDAI data through `getAllSdxl`.
     *
     * @return Result produced by `getAllSdxl`.
     * @author Dmitriy Moroz
     */
    override suspend fun getAllSdxl(): List<LocalAiModel> {
        refreshCache()
        return localDataSource.getAllSdxl()
    }

    /**
     * Loads SDAI data through `getAllCoreMl`.
     *
     * @return Result produced by `getAllCoreMl`.
     * @author Dmitriy Moroz
     */
    override suspend fun getAllCoreMl(): List<LocalAiModel> {
        refreshCache()
        return localDataSource.getAllCoreMl()
    }

    /**
     * Loads SDAI data through `getAllBonsai`.
     *
     * @return Result produced by `getAllBonsai`.
     * @author Dmitriy Moroz
     */
    override suspend fun getAllBonsai(): List<LocalAiModel> {
        refreshCache()
        return localDataSource.getAllBonsai()
    }

    /**
     * Loads SDAI data through `observeAllOnnx`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeAllOnnx() = localDataSource.observeAllOnnx()

    /**
     * Loads SDAI data through `observeAllSdxl`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeAllSdxl() = localDataSource.observeAllSdxl()

    /**
     * Loads SDAI data through `observeAllCoreMl`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeAllCoreMl() = localDataSource.observeAllCoreMl()

    /**
     * Loads SDAI data through `observeAllBonsai`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeAllBonsai() = localDataSource.observeAllBonsai()

    /**
     * Executes the `refreshCache` step in the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private suspend fun refreshCache() {
        runCatching {
            localDataSource.save(remoteDataSource.fetch())
        }
    }
}
