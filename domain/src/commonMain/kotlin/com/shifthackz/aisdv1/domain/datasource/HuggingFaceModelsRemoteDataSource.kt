package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

fun interface HuggingFaceModelsRemoteDataSource {

    suspend fun fetchHuggingFaceModels(): List<HuggingFaceModel>
}
