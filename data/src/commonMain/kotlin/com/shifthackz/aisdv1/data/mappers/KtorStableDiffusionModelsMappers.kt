package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.network.model.KtorStableDiffusionModelRaw

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun List<KtorStableDiffusionModelRaw>.mapKtorRawToCheckpointDomain(): List<StableDiffusionModel> =
    map(KtorStableDiffusionModelRaw::mapKtorRawToCheckpointDomain)

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun KtorStableDiffusionModelRaw.mapKtorRawToCheckpointDomain(): StableDiffusionModel =
    StableDiffusionModel(
        title = title ?: "",
        modelName = modelName ?: "",
        hash = hash ?: "",
        sha256 = sha256 ?: "",
        filename = filename ?: "",
        config = config ?: "",
    )
