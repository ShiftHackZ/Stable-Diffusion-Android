package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel

interface StableDiffusionModelsRepository {
    suspend fun fetchModels()
    suspend fun fetchAndGetModels(): List<StableDiffusionModel>
    suspend fun getModels(): List<StableDiffusionModel>
}
