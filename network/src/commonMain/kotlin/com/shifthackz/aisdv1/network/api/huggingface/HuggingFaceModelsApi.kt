package com.shifthackz.aisdv1.network.api.huggingface

import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw

interface HuggingFaceModelsApi {
    suspend fun fetchTextToImageModels(): List<HuggingFaceModelRaw>
}
