package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.file.unzip
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.network.api.sdai.DownloadableModelsRestApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.io.File

internal class DownloadableModelRemoteDataSource(
    private val api: DownloadableModelsRestApi,
    private val fileProviderDescriptor: FileProviderDescriptor,
) : DownloadableModelDataSource.Remote {

    private val destinationPath = "${fileProviderDescriptor.localModelDirPath}/model.zip"

    override fun download() = Completable
        .fromAction {
            val dir = File(fileProviderDescriptor.localModelDirPath)
            val destination = File(destinationPath)
            if (destination.exists()) destination.delete()
            if (!dir.exists()) dir.mkdirs()
        }
        .andThen(
            api.downloadModel(
                "${fileProviderDescriptor.localModelDirPath}/model.zip",
                stateProgress = DownloadState::Downloading,
                stateComplete = DownloadState::Complete,
                stateFailed = DownloadState::Error,
            )
        )
        .flatMap { state ->
            val chain = Observable.just(state)
            if (state is DownloadState.Complete) {
                Completable
                    .create { emitter ->
                        try {
                            state.file.unzip()
                            emitter.onComplete()
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    }
                    .andThen(Completable.fromAction { File(destinationPath).delete() })
                    .andThen(chain)
            } else {
                chain
            }
        }
}
