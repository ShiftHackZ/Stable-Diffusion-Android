package com.shifthackz.aisdv1.network.api.huggingface

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom

class KtorHuggingFaceModelsApi(
    private val httpClient: HttpClient,
    private val apiBaseUrl: String,
) : HuggingFaceModelsApi {

    constructor(
        apiBaseUrl: String,
    ) : this(
        httpClient = createConfiguredHttpClient(),
        apiBaseUrl = apiBaseUrl,
    )

    override suspend fun fetchTextToImageModels(): List<HuggingFaceModelRaw> = httpClient
        .get {
            url.takeFrom(apiBaseUrl)
            url.appendPathSegments(PATH_API, PATH_MODELS)
            parameter(QUERY_INFERENCE_PROVIDER, PROVIDER_HF_INFERENCE)
            parameter(QUERY_PIPELINE_TAG, PIPELINE_TEXT_TO_IMAGE)
            parameter(QUERY_LIMIT, QUERY_LIMIT_VALUE)
        }
        .body()

    private companion object {
        const val PATH_API = "api"
        const val PATH_MODELS = "models"
        const val QUERY_INFERENCE_PROVIDER = "inference_provider"
        const val QUERY_PIPELINE_TAG = "pipeline_tag"
        const val QUERY_LIMIT = "limit"
        const val PROVIDER_HF_INFERENCE = "hf-inference"
        const val PIPELINE_TEXT_TO_IMAGE = "text-to-image"
        const val QUERY_LIMIT_VALUE = 50
    }
}
