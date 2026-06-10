package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface HuggingFaceModelsRemoteDataSource {

    suspend fun fetchHuggingFaceModels(): List<HuggingFaceModel>
}
