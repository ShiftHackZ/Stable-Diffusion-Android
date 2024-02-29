package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.extensions.saveFile
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File

internal class DownloadableModelsApiImpl(
    private val rawApi: DownloadableModelsApi.RawApi,
) : DownloadableModelsApi {

    override fun fetchDownloadableModels() = rawApi.fetchDownloadableModels()

    override fun <T : Any> downloadModel(
        remoteUrl: String,
        localPath: String,
        stateProgress: (Int) -> T,
        stateComplete: (File) -> T,
        stateFailed: (Throwable) -> T
    ): Observable<T> = Single
        .just(remoteUrl)
        .flatMap(rawApi::downloadModel)
        .flatMapObservable { body ->
            body.saveFile(localPath, stateProgress, stateComplete, stateFailed)
        }
}
