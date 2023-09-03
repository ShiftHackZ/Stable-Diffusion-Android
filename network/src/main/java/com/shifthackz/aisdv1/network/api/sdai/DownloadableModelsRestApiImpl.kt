package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.extensions.saveFile
import io.reactivex.rxjava3.core.Observable
import java.io.File

internal class DownloadableModelsRestApiImpl(
    private val rawApi: DownloadableModelsRestApi.RawApi,
) : DownloadableModelsRestApi {

    override fun <T : Any> downloadModel(
        path: String,
        stateProgress: (Int) -> T,
        stateComplete: (File) -> T,
        stateFailed: (Throwable) -> T
    ): Observable<T> = rawApi
        .fetchDownloadableModels()
        .map { models -> models.first().sources?.first() ?: "" }
        .flatMap(rawApi::downloadModel)
        .flatMapObservable { body ->
            body.saveFile(path, stateProgress, stateComplete, stateFailed)
        }
}
