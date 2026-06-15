package com.shifthackz.aisdv1.network.api.arliai

import com.shifthackz.aisdv1.network.model.KtorStableDiffusionModelRaw
import com.shifthackz.aisdv1.network.request.ArliAiImageToImageRequest
import com.shifthackz.aisdv1.network.request.ArliAiTextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse

/**
 * Describes the ArliAI SDNext-compatible generation endpoints.
 *
 * Implementations use bearer authorization and return Automatic1111-style
 * generation responses so the data layer can reuse Stable Diffusion mappers.
 *
 * @author Dmitriy Moroz
 */
interface ArliAiGenerationApi {
    /**
     * Verifies that the supplied ArliAI key can access the provider API.
     *
     * @param apiKey ArliAI API key sent as bearer authorization.
     *
     * @author Dmitriy Moroz
     */
    suspend fun validateApiKey(apiKey: String)

    /**
     * Loads the checkpoint list exposed by ArliAI.
     *
     * @param apiKey ArliAI API key sent as bearer authorization.
     * @return raw checkpoint metadata returned by the provider.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchModels(apiKey: String): List<KtorStableDiffusionModelRaw>

    /**
     * Sends a text-to-image request to ArliAI.
     *
     * @param apiKey ArliAI API key sent as bearer authorization.
     * @param request SDNext-compatible text-to-image payload.
     * @return generated image payload returned by ArliAI.
     *
     * @author Dmitriy Moroz
     */
    suspend fun textToImage(
        apiKey: String,
        request: ArliAiTextToImageRequest,
    ): SdGenerationResponse

    /**
     * Sends an image-to-image request to ArliAI.
     *
     * @param apiKey ArliAI API key sent as bearer authorization.
     * @param request SDNext-compatible image-to-image payload.
     * @return generated image payload returned by ArliAI.
     *
     * @author Dmitriy Moroz
     */
    suspend fun imageToImage(
        apiKey: String,
        request: ArliAiImageToImageRequest,
    ): SdGenerationResponse
}
