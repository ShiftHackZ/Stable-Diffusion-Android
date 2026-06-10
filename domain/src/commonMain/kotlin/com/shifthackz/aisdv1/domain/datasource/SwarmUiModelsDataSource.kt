package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel

interface SwarmUiModelsDataSource {

    interface Local : SwarmUiModelsDataSource {
        suspend fun getModels(): List<SwarmUiModel>
        suspend fun insertModels(models: List<SwarmUiModel>)
    }
}
