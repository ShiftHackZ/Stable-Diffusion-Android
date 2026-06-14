package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.storage.db.persistent.dao.LocalModelDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Reads local model metadata and combines it with filesystem download state.
 *
 * @param dao Room DAO that stores local model metadata.
 * @param preferenceManager Preferences source for currently selected local model ids.
 * @param buildInfoProvider Build metadata used to filter custom model placeholders.
 * @param fileStore Platform file store that resolves whether model files are downloaded.
 * @throws IllegalStateException when delegated local model IO cannot complete.
 *
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
     * Loads SDAI data through `getAllSdxl`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getAllSdxl() = dao
        .queryByType(LocalAiModel.Type.Sdxl.key)
        .mapEntityToDomain()
        .let { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type != BuildType.PLAY) {
                    add(LocalAiModel.CustomSdxl)
                }
            }
        }
        .withLocalData()

    /**
     * Loads SDAI data through `getAllCoreMl`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getAllCoreMl() = dao
        .queryByType(LocalAiModel.Type.CoreMl.key)
        .mapEntityToDomain()
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
            LocalAiModel.CustomSdxl.id -> LocalAiModel.CustomSdxl
            LocalAiModel.CustomCoreMl.id -> LocalAiModel.CustomCoreMl
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
     * Loads SDAI data through `getSelectedSdxl`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getSelectedSdxl() = runCatching {
        getById(preferenceManager.localSdxlModelId)
    }.getOrElse {
        throw IllegalStateException("No selected model.", it)
    }

    /**
     * Loads SDAI data through `getSelectedCoreMl`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getSelectedCoreMl() = runCatching {
        getById(preferenceManager.localCoreMlModelId)
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
        .combine(preferenceManager.observe()) { entities, _ ->
            buildList {
                addAll(entities.mapEntityToDomain())
                if (buildInfoProvider.type != BuildType.PLAY) add(LocalAiModel.CustomOnnx)
            }
                .withLocalData()
        }

    /**
     * Loads SDAI data through `observeAllSdxl`.
     *
     * @return Result produced by `observeAllSdxl`.
     * @author Dmitriy Moroz
     */
    override fun observeAllSdxl(): Flow<List<LocalAiModel>> = dao
        .observeByType(LocalAiModel.Type.Sdxl.key)
        .combine(preferenceManager.observe()) { entities, _ ->
            buildList {
                addAll(entities.mapEntityToDomain())
                if (buildInfoProvider.type != BuildType.PLAY) {
                    add(LocalAiModel.CustomSdxl)
                }
            }
                .withLocalData()
        }

    /**
     * Loads SDAI data through `observeAllCoreMl`.
     *
     * @return Result produced by `observeAllCoreMl`.
     * @author Dmitriy Moroz
     */
    override fun observeAllCoreMl(): Flow<List<LocalAiModel>> = dao
        .observeByType(LocalAiModel.Type.CoreMl.key)
        .combine(preferenceManager.observe()) { entities, _ ->
            entities
                .mapEntityToDomain()
                .withLocalData()
        }

    /**
     * Performs the SDAI side effect handled by `save`.
     *
     * @param list list value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun save(list: List<LocalAiModel>) {
        dao.insertList(
            list
                .filterNot { it.id in customModelIds }
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
            LocalAiModel.Type.Sdxl -> preferenceManager.localSdxlModelId == id
            LocalAiModel.Type.CoreMl -> preferenceManager.localCoreMlModelId == id
        },
    )

    private companion object {
        val customModelIds = setOf(
            LocalAiModel.CustomOnnx.id,
            LocalAiModel.CustomMediaPipe.id,
            LocalAiModel.CustomSdxl.id,
            LocalAiModel.CustomCoreMl.id,
        )
    }
}
