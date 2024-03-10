package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.data.mappers.mapCloudToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.data.mappers.mapToStabilityAiRequest
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiApi
import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw
import com.shifthackz.aisdv1.network.response.StabilityResponse
import io.reactivex.rxjava3.core.Single

internal class StabilityAiGenerationRemoteDataSource(
    private val api: StabilityAiApi,
) : StabilityAiGenerationDataSource.Remote {

    override fun validateApiKey() = api
        .validateBearerToken()
        .andThen(Single.just(true))
        .onErrorResumeNext { t ->
            errorLog(t)
            Single.just(false)
        }

    override fun fetchEngines() = api
        .fetchEngines()
        .map(List<StabilityAiEngineRaw>::mapRawToDomain)

    override fun textToImage(engineId: String, payload: TextToImagePayload) = api
        .textToImage(engineId, payload.mapToStabilityAiRequest())
        .flatMap { it.processResponse(payload) }
        .map(Pair<TextToImagePayload, String>::mapCloudToAiGenResult)

    override fun imageToImage(engineId: String, payload: ImageToImagePayload): Single<AiGenerationResult> = api
        .imageToImage(engineId, payload.mapToStabilityAiRequest())
        .flatMap { it.processResponse(payload) }
        .map(Pair<ImageToImagePayload, String>::mapCloudToAiGenResult)

    private fun <T: Any> StabilityResponse.processResponse(payload: T): Single<Pair<T, String>> {
        return artifacts?.firstOrNull()?.base64?.let { base64 ->
            Single.just(payload to base64)
        } ?: run {
            Single.error(IllegalStateException("Got null data object from API."))
        }
    }
}
