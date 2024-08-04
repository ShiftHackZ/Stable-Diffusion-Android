package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.mappers.mapCloudToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToSwarmUiRequest
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi.Companion.PATH_GENERATE
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import io.reactivex.rxjava3.core.Single

class SwarmUiGenerationRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: SwarmUiApi,
    private val converter: BitmapToBase64Converter,
) : SwarmUiGenerationDataSource.Remote {

    override fun textToImage(
        sessionId: String,
        model: String,
        payload: TextToImagePayload
    ): Single<AiGenerationResult> =
        generate(
            payload = payload,
            request = payload.mapToSwarmUiRequest(sessionId, model),
        )
        .map(Pair<TextToImagePayload, String>::mapCloudToAiGenResult)

    override fun imageToImage(
        sessionId: String,
        model: String,
        payload: ImageToImagePayload,
    ): Single<AiGenerationResult> =
        generate(
            payload = payload,
            request = payload.mapToSwarmUiRequest(sessionId, model),
        )
        .map(Pair<ImageToImagePayload, String>::mapCloudToAiGenResult)

    private fun <T: Any> generate(
        payload: T,
        request: SwarmUiGenerationRequest,
    ): Single<Pair<T, String>> = serverUrlProvider(PATH_GENERATE)
        .flatMap { url -> api.generate(url, request) }
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
}
