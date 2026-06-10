package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

interface HuggingFaceModelsRepository {
    suspend fun fetchHuggingFaceModels()
    suspend fun fetchAndGetHuggingFaceModels(): List<HuggingFaceModel>
    suspend fun getHuggingFaceModels(): List<HuggingFaceModel>
}
