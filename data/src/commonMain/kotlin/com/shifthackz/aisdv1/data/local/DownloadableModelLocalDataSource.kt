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

internal class DownloadableModelLocalDataSource(
    private val dao: LocalModelDao,
    private val preferenceManager: PreferenceManager,
    private val buildInfoProvider: BuildInfoProvider,
    private val fileStore: DownloadableModelFileStore,
) : DownloadableModelDataSource.Local {

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

    override suspend fun getSelectedOnnx() = runCatching {
        getById(preferenceManager.localOnnxModelId)
    }.getOrElse {
        throw IllegalStateException("No selected model.", it)
    }

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

    override suspend fun save(list: List<LocalAiModel>) {
        dao.insertList(
            list
                .filter { it.id != LocalAiModel.CustomOnnx.id }
                .mapDomainToEntity(),
        )
    }

    override suspend fun delete(id: String) {
        fileStore.delete(id)
    }

    private fun List<LocalAiModel>.withLocalData() = map { model -> model.withLocalData() }

    private fun LocalAiModel.withLocalData() = copy(
        downloaded = fileStore.isDownloaded(this),
        selected = when (this.type) {
            LocalAiModel.Type.ONNX -> preferenceManager.localOnnxModelId == id
            LocalAiModel.Type.MediaPipe -> preferenceManager.localMediaPipeModelId == id
        },
    )
}
