package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

sealed interface StableDiffusionModelsDataSource {

    interface Remote : StableDiffusionModelsDataSource {
        suspend fun fetchSdModels(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        ): List<StableDiffusionModel>
    }

    interface Local : StableDiffusionModelsDataSource {
        suspend fun getModels(): List<StableDiffusionModel>
        suspend fun insertModels(models: List<StableDiffusionModel>)
    }
}
