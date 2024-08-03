package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.mappers.mapCloudToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToSwarmUiRequest
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi.Companion.PATH_SESSION
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi.Companion.PATH_TXT_TO_IMG
import io.reactivex.rxjava3.core.Single

class SwarmUiGenerationRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: SwarmUiApi,
    private val converter: BitmapToBase64Converter,
) : SwarmUiGenerationDataSource.Remote {

    override fun getNewSession() = PATH_SESSION
        .let(serverUrlProvider::invoke)
        .flatMap(::getSessionForUrl)

    override fun getNewSession(url: String) =
        getSessionForUrl("${url.fixUrlSlashes()}/$PATH_SESSION")

    override fun textToImage(
        sessionId: String,
        payload: TextToImagePayload,
    ) = serverUrlProvider(PATH_TXT_TO_IMG)
        .flatMap { url -> api.textToImage(url, payload.mapToSwarmUiRequest(sessionId)) }
        .flatMap { response ->
            serverUrlProvider("").map { url -> response to url }
        }
        .flatMap { (response, url) ->
            response.images
                ?.firstOrNull()
                ?.let { endpoint -> Single.just("$url/$endpoint".fixUrlSlashes()) }
                ?: Single.error(IllegalStateException("Bad response"))
        }
        .flatMap(api::downloadImage)
        .map(BitmapToBase64Converter::Input)
        .flatMap(converter::invoke)
        .map(BitmapToBase64Converter.Output::base64ImageString)
        .map { base64 -> payload to base64 }
        .map(Pair<TextToImagePayload, String>::mapCloudToAiGenResult)

    private fun getSessionForUrl(url: String) = api
        .getNewSession(url)
        .flatMap { response ->
            response.sessionId
                ?.takeIf(String::isNotBlank)
                ?.let { Single.just(it) }
                ?: Single.error(IllegalStateException("Bad session ID."))
        }
}
