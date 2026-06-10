package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.storage.db.persistent.dao.LocalModelDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Coordinates `DownloadableModelLocalDataSource` behavior in the SDAI data layer.
 *
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
internal class DownloadableModelLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val dao: LocalModelDao,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider,
    /**
     * Exposes the `fileStore` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val fileStore: DownloadableModelFileStore,
) : DownloadableModelDataSource.Local {

    /**
     * Loads SDAI data through `getAllOnnx`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getAllOnnx() = dao
        .queryByType(LocalAiModel.Type.ONNX.key)
        .mapEntityToDomain()
        .let { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type != BuildType.PLAY) {
                    add(LocalAiModel.CustomOnnx)
                }
            }
        }
        .withLocalData()

    /**
     * Loads SDAI data through `getAllMediaPipe`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getAllMediaPipe() = dao
        .queryByType(LocalAiModel.Type.MediaPipe.key)
        .mapEntityToDomain()
        .let { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type != BuildType.PLAY) {
                    add(LocalAiModel.CustomMediaPipe)
                }
            }
        }
        .withLocalData()

    /**
     * Loads SDAI data through `getById`.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `getById`.
     * @author Dmitriy Moroz
     */
    override suspend fun getById(id: String): LocalAiModel {
        val chain = when (id) {
            LocalAiModel.CustomOnnx.id -> LocalAiModel.CustomOnnx
            LocalAiModel.CustomMediaPipe.id -> LocalAiModel.CustomMediaPipe
            else -> dao
                .queryById(id)
                .mapEntityToDomain()
        }
        return chain.withLocalData()
    }

    /**
     * Loads SDAI data through `getSelectedOnnx`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getSelectedOnnx() = runCatching {
        getById(preferenceManager.localOnnxModelId)
    }.getOrElse {
        throw IllegalStateException("No selected model.", it)
    }

    /**
     * Loads SDAI data through `observeAllOnnx`.
     *
     * @return Result produced by `observeAllOnnx`.
     * @author Dmitriy Moroz
     */
    override fun observeAllOnnx(): Flow<List<LocalAiModel>> = dao
        .observeByType(LocalAiModel.Type.ONNX.key)
        .map(List<LocalModelEntity>::mapEntityToDomain)
        .map { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type != BuildType.PLAY) add(LocalAiModel.CustomOnnx)
            }
        }
        .map { models -> models.withLocalData() }

    /**
     * Performs the SDAI side effect handled by `save`.
     *
     * @param list list value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun save(list: List<LocalAiModel>) {
        dao.insertList(
            list
                .filter { it.id != LocalAiModel.CustomOnnx.id }
                .mapDomainToEntity(),
        )
    }

    /**
     * Performs the SDAI side effect handled by `delete`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override suspend fun delete(id: String) {
        fileStore.delete(id)
    }

    /**
     * Executes the `withLocalData` step in the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private fun List<LocalAiModel>.withLocalData() = map { model -> model.withLocalData() }

    /**
     * Executes the `withLocalData` step in the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private fun LocalAiModel.withLocalData() = copy(
        downloaded = fileStore.isDownloaded(this),
        selected = when (this.type) {
            LocalAiModel.Type.ONNX -> preferenceManager.localOnnxModelId == id
            LocalAiModel.Type.MediaPipe -> preferenceManager.localMediaPipeModelId == id
        },
    )
}
