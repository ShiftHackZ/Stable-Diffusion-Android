package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionSamplerRaw>.mapKtorRawToCheckpointDomain(): List<StableDiffusionSampler> =
    map(StableDiffusionSamplerRaw::mapKtorRawToCheckpointDomain)

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun StableDiffusionSamplerRaw.mapKtorRawToCheckpointDomain(): StableDiffusionSampler =
    StableDiffusionSampler(
        name = name ?: "",
        aliases = aliases ?: emptyList(),
        options = options ?: emptyMap(),
    )
