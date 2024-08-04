package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.data.mappers.mapCloudToAiGenResult
import com.shifthackz.aisdv1.data.mappers.mapToOpenAiRequest
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.api.openai.OpenAiApi
import io.reactivex.rxjava3.core.Single
import java.lang.IllegalStateException

internal class OpenAiGenerationRemoteDataSource(
    private val api: OpenAiApi,
) : OpenAiGenerationDataSource.Remote {

    override fun validateApiKey(): Single<Boolean> = api
        .validateBearerToken()
        .andThen(Single.just(true))
        .onErrorResumeNext { t ->
            errorLog(t)
            Single.just(false)
        }

    override fun textToImage(payload: TextToImagePayload): Single<AiGenerationResult> = payload
        .mapToOpenAiRequest()
        .let(api::generateImage)
        .flatMap { response ->
            response.data?.firstOrNull()?.b64json?.let { base64 ->
                Single.just(payload to base64)
            } ?: run {
                Single.error(IllegalStateException("Got null data object from API."))
            }
        }
        .map(Pair<TextToImagePayload, String>::mapCloudToAiGenResult)
}
