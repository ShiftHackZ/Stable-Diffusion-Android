package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel

interface SwarmUiModelsRepository {

    suspend fun fetchModels()
    suspend fun fetchAndGetModels(): List<SwarmUiModel>
    suspend fun getModels(): List<SwarmUiModel>
}
