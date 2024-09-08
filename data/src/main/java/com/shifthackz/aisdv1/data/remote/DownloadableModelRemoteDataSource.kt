package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.file.unzip
import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.network.api.sdai.DownloadableModelsApi
import com.shifthackz.aisdv1.network.response.DownloadableModelResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File

internal class DownloadableModelRemoteDataSource(
    private val api: DownloadableModelsApi,
    private val fileProviderDescriptor: FileProviderDescriptor,
) : DownloadableModelDataSource.Remote {

    override fun fetch(): Single<List<LocalAiModel>> = Single.zip(
        api
            .fetchOnnxModels()
            .map { it.mapRawToCheckpointDomain(LocalAiModel.Type.ONNX) },
        api
            .fetchMediaPipeModels()
            .map { it.mapRawToCheckpointDomain(LocalAiModel.Type.MediaPipe) },
        ::Pair,
    )
        .map { (onnx, mediapipe) -> listOf(onnx, mediapipe).flatten() }

    override fun download(id: String, url: String): Observable<DownloadState> = Completable
        .fromAction {
            val dir = File("${fileProviderDescriptor.localModelDirPath}/${id}")
            val destination = File(getDestinationPath(id))
            if (destination.exists()) destination.delete()
            if (!dir.exists()) dir.mkdirs()
        }
        .andThen(
            api.downloadModel(
                remoteUrl = url,
                localPath = getDestinationPath(id),
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
                    .andThen(Completable.fromAction { File(getDestinationPath(id)).delete() })
                    .andThen(chain)
            } else {
                chain
            }
        }

    private fun getDestinationPath(id: String): String {
        return "${fileProviderDescriptor.localModelDirPath}/${id}/model.zip"
    }
}
