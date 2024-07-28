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

    override fun getAll(): Single<List<LocalAiModel>> = dao.query()
        .map(List<LocalModelEntity>::mapEntityToDomain)
        .map { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type == BuildType.FOSS) add(LocalAiModel.CUSTOM)
            }
        }
        .flatMap { models -> models.withLocalData() }

    override fun getById(id: String): Single<LocalAiModel> {
        val chain = if (id == LocalAiModel.CUSTOM.id) Single.just(LocalAiModel.CUSTOM)
        else dao
            .queryById(id)
            .map(LocalModelEntity::mapEntityToDomain)

        return chain.flatMap { model -> model.withLocalData() }
    }

    override fun getSelected(): Single<LocalAiModel> = Single
        .just(preferenceManager.localModelId)
        .onErrorResumeNext { Single.error(IllegalStateException("No selected model.")) }
        .flatMap(::getById)
        .onErrorResumeNext { Single.error(IllegalStateException("No selected model.")) }

    override fun observeAll(): Flowable<List<LocalAiModel>> = dao
        .observe()
        .map(List<LocalModelEntity>::mapEntityToDomain)
        .map { models ->
            buildList {
                addAll(models)
                if (buildInfoProvider.type == BuildType.FOSS) add(LocalAiModel.CUSTOM)
            }
        }
        .flatMap { models -> models.withLocalData().toFlowable() }

    override fun select(id: String): Completable = Completable.fromAction {
        preferenceManager.localModelId = id
    }

    override fun save(list: List<LocalAiModel>) = list
        .filter { it.id != LocalAiModel.CUSTOM.id }
        .mapDomainToEntity()
        .let(dao::insertList)

    override fun isDownloaded(id: String): Single<Boolean> = Single.create { emitter ->
        try {
            if (id == LocalAiModel.CUSTOM.id) {
                if (!emitter.isDisposed) emitter.onSuccess(true)
            } else {
                val localModelDir = getLocalModelDirectory(id)
                val files =
                    (localModelDir.listFiles()?.filter { it.isDirectory }) ?: emptyList<File>()
                if (!emitter.isDisposed) emitter.onSuccess(localModelDir.exists() && files.size == 4)
            }
        } catch (e: Exception) {
            if (!emitter.isDisposed) emitter.onSuccess(false)
        }
    }

    override fun delete(id: String): Completable = Completable.fromAction {
        getLocalModelDirectory(id).deleteRecursively()
    }

    private fun getLocalModelDirectory(id: String): File {
        return File("${fileProviderDescriptor.localModelDirPath}/${id}")
    }

    private fun List<LocalAiModel>.withLocalData(): Single<List<LocalAiModel>> = Observable
        .fromIterable(this)
        .flatMapSingle { model -> model.withLocalData() }
        .toList()

    private fun LocalAiModel.withLocalData(): Single<LocalAiModel> = isDownloaded(id)
        .map { downloaded ->
            copy(
                downloaded = downloaded,
                selected = preferenceManager.localModelId == id,
            )
        }
}
