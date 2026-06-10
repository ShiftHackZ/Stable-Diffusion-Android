package com.shifthackz.aisdv1.network.api.huggingface

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom

/**
 * Coordinates `KtorHuggingFaceModelsApi` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class KtorHuggingFaceModelsApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient,
    /**
     * Exposes the `apiBaseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val apiBaseUrl: String,
) : HuggingFaceModelsApi {

    /**
     * Creates a new SDAI component instance.
     *
     * @param apiBaseUrl api base url value consumed by the API.
     * @author Dmitriy Moroz
     */
    constructor(
        apiBaseUrl: String,
    ) : this(
        httpClient = createConfiguredHttpClient(),
        apiBaseUrl = apiBaseUrl,
    )

    /**
     * Loads SDAI data through `fetchTextToImageModels`.
     *
     * @return Result produced by `fetchTextToImageModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchTextToImageModels(): List<HuggingFaceModelRaw> = httpClient
        .get {
            url.takeFrom(apiBaseUrl)
            url.appendPathSegments(PATH_API, PATH_MODELS)
            parameter(QUERY_INFERENCE_PROVIDER, PROVIDER_HF_INFERENCE)
            parameter(QUERY_PIPELINE_TAG, PIPELINE_TEXT_TO_IMAGE)
            parameter(QUERY_LIMIT, QUERY_LIMIT_VALUE)
        }
        .body()

    /**
     * Provides the `companion object` singleton used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `PATH_API` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_API = "api"
        /**
         * Exposes the `PATH_MODELS` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PATH_MODELS = "models"
        /**
         * Exposes the `QUERY_INFERENCE_PROVIDER` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val QUERY_INFERENCE_PROVIDER = "inference_provider"
        /**
         * Exposes the `QUERY_PIPELINE_TAG` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val QUERY_PIPELINE_TAG = "pipeline_tag"
        /**
         * Exposes the `QUERY_LIMIT` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val QUERY_LIMIT = "limit"
        /**
         * Exposes the `PROVIDER_HF_INFERENCE` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PROVIDER_HF_INFERENCE = "hf-inference"
        /**
         * Exposes the `PIPELINE_TEXT_TO_IMAGE` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val PIPELINE_TEXT_TO_IMAGE = "text-to-image"
        /**
         * Exposes the `QUERY_LIMIT_VALUE` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val QUERY_LIMIT_VALUE = 50
    }
}
