package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

sealed interface HuggingFaceModelsDataSource {

    interface Local : HuggingFaceModelsDataSource {
        suspend fun getAll(): List<HuggingFaceModel>
        suspend fun save(models: List<HuggingFaceModel>)
    }
}
