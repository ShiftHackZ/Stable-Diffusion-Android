package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.storage.db.persistent.dao.LocalModelDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File

internal class DownloadableModelLocalDataSource(
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val dao: LocalModelDao,
    private val preferenceManager: PreferenceManager,
    private val buildInfoProvider: BuildInfoProvider,
) : DownloadableModelDataSource.Local {

    override fun getAllOnnx() = dao
        .queryByType(LocalAiModel.Type.ONNX.key)
        .map(List<LocalModelEntity>::mapEntityToDomain)
        .map { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type != BuildType.PLAY) {
                    add(LocalAiModel.CustomOnnx)
                }
            }
        }
        .flatMap { models -> models.withLocalData() }

    override fun getAllMediaPipe(): Single<List<LocalAiModel>> = dao
        .queryByType(LocalAiModel.Type.MediaPipe.key)
        .map(List<LocalModelEntity>::mapEntityToDomain)
        .map { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type != BuildType.PLAY) {
                    add(LocalAiModel.CustomMediaPipe)
                }
            }
        }
        .flatMap { models -> models.withLocalData() }

    override fun getById(id: String): Single<LocalAiModel> {
        val chain = when (id) {
            LocalAiModel.CustomOnnx.id -> Single.just(LocalAiModel.CustomOnnx)
            LocalAiModel.CustomMediaPipe.id -> Single.just(LocalAiModel.CustomMediaPipe)
            else -> dao
                .queryById(id)
                .map(LocalModelEntity::mapEntityToDomain)
        }
        return chain.flatMap { model -> model.withLocalData() }
    }

    override fun getSelectedOnnx() = Single
        .just(preferenceManager.localOnnxModelId)
        .flatMap(::getById)
        .onErrorResumeNext { Single.error(IllegalStateException("No selected model.")) }

    override fun observeAllOnnx(): Flowable<List<LocalAiModel>> = dao
        .observeByType(LocalAiModel.Type.ONNX.key)
        .map(List<LocalModelEntity>::mapEntityToDomain)
        .map { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type != BuildType.PLAY) add(LocalAiModel.CustomOnnx)
            }
        }
        .flatMap { models -> models.withLocalData().toFlowable() }

    override fun save(list: List<LocalAiModel>) = list
        .filter { it.id != LocalAiModel.CustomOnnx.id }
        .mapDomainToEntity()
        .let(dao::insertList)

    override fun delete(id: String): Completable = Completable.fromAction {
        getLocalModelDirectory(id).deleteRecursively()
    }

    private fun isDownloaded(model: LocalAiModel) = Single.create { emitter ->
        try {
            when (model.id) {
                LocalAiModel.CustomOnnx.id,
                LocalAiModel.CustomMediaPipe.id -> emitter.onSuccess(true)

                else -> {

                    when (model.type) {
                        LocalAiModel.Type.ONNX -> {
                            val files = getLocalModelFiles(model.id).filter { it.isDirectory }
                            emitter.onSuccess(files.size == 4)
                        }

                        LocalAiModel.Type.MediaPipe -> {
                            val files = getLocalModelFiles(model.id)
                            emitter.onSuccess(files.isNotEmpty())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            if (!emitter.isDisposed) emitter.onSuccess(false)
        }
    }

    private fun getLocalModelDirectory(id: String): File {
        return File("${fileProviderDescriptor.localModelDirPath}/${id}")
    }

    private fun getLocalModelFiles(id: String): List<File> {
        val localModelDir = getLocalModelDirectory(id)
        if (!localModelDir.exists()) return emptyList()
        return localModelDir.listFiles()?.toList() ?: emptyList()
    }

    private fun List<LocalAiModel>.withLocalData() = Observable
        .fromIterable(this)
        .flatMapSingle { model -> model.withLocalData() }
        .toList()

    private fun LocalAiModel.withLocalData() = isDownloaded(this)
        .map { downloaded ->
            copy(
                downloaded = downloaded,
                selected = when (this.type) {
                    LocalAiModel.Type.ONNX -> preferenceManager.localOnnxModelId == id
                    LocalAiModel.Type.MediaPipe -> preferenceManager.localMediaPipeModelId == id
                },
            )
        }
}
