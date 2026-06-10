package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw

fun List<StableDiffusionSamplerRaw>.mapKtorRawToCheckpointDomain(): List<StableDiffusionSampler> =
    map(StableDiffusionSamplerRaw::mapKtorRawToCheckpointDomain)

fun StableDiffusionSamplerRaw.mapKtorRawToCheckpointDomain(): StableDiffusionSampler =
    StableDiffusionSampler(
        name = name ?: "",
        aliases = aliases ?: emptyList(),
        options = options ?: emptyMap(),
    )
