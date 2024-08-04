package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.data.mappers.mapCloudToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToStabilityAiRequest
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiApi
import com.shifthackz.aisdv1.network.error.StabilityAiErrorMapper
import com.shifthackz.aisdv1.network.response.StabilityGenerationResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

internal class StabilityAiGenerationRemoteDataSource(
    private val api: StabilityAiApi,
    private val stabilityAiErrorMapper: StabilityAiErrorMapper,
) : StabilityAiGenerationDataSource.Remote {

    override fun validateApiKey(): Single<Boolean> = api
        .validateBearerToken()
        .andThen(Single.just(true))
        .onErrorResumeNext { t ->
            errorLog(t)
            Single.just(false)
        }

    override fun textToImage(engineId: String, payload: TextToImagePayload): Single<AiGenerationResult> = api
        .textToImage(engineId, payload.mapToStabilityAiRequest())
        .flatMap { it.processResponse(payload) }
        .map(Pair<TextToImagePayload, String>::mapCloudToAiGenResult)
        .onErrorResumeNext { t -> stabilityAiErrorMapper(t) }

    // Fuck you 🖕 Stability AI for not accepting base64 as img2img input
    // as any another normal backend does...
    override fun imageToImage(
        engineId: String,
        payload: ImageToImagePayload,
        imageBytes: ByteArray,
    ): Single<AiGenerationResult> {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "init_image",
                filename = "image.png",
                body = imageBytes.toRequestBody(
                    contentType = "image/png".toMediaTypeOrNull(),
                    offset = 0,
                    byteCount = imageBytes.size,
                ),
            )

        val params = payload.mapToStabilityAiRequest()
        params.forEach { (t, u) -> builder.addFormDataPart(t, u) }

        return api
            .imageToImage(engineId, builder.build())
            .flatMap { it.processResponse(payload) }
            .map(Pair<ImageToImagePayload, String>::mapCloudToAiGenResult)
            .onErrorResumeNext { t -> stabilityAiErrorMapper(t) }
    }

    private fun <T: Any> StabilityGenerationResponse.processResponse(payload: T): Single<Pair<T, String>> {
        return artifacts?.firstOrNull()?.base64?.let { base64 ->
            Single.just(payload to base64)
        } ?: run {
            Single.error(IllegalStateException("Got null data object from API."))
        }
    }
}
