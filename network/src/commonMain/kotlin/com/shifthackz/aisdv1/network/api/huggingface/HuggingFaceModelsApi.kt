package com.shifthackz.aisdv1.network.api.huggingface

import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw

/**
 * Defines the `HuggingFaceModelsApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface HuggingFaceModelsApi {
    /**
     * Loads SDAI data through `fetchTextToImageModels`.
     *
     * @return Result produced by `fetchTextToImageModels`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchTextToImageModels(): List<HuggingFaceModelRaw>
}
