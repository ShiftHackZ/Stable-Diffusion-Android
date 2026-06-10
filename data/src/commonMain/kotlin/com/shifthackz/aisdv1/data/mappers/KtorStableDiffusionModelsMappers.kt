package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.network.model.KtorStableDiffusionModelRaw

fun List<KtorStableDiffusionModelRaw>.mapKtorRawToCheckpointDomain(): List<StableDiffusionModel> =
    map(KtorStableDiffusionModelRaw::mapKtorRawToCheckpointDomain)

fun KtorStableDiffusionModelRaw.mapKtorRawToCheckpointDomain(): StableDiffusionModel =
    StableDiffusionModel(
        title = title ?: "",
        modelName = modelName ?: "",
        hash = hash ?: "",
        sha256 = sha256 ?: "",
        filename = filename ?: "",
        config = config ?: "",
    )
