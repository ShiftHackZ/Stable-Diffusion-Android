package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSamplerDomain
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity

//region RAW --> DOMAIN
fun List<StableDiffusionSamplerRaw>.mapToDomain(): List<StableDiffusionSamplerDomain> =
    map(StableDiffusionSamplerRaw::mapToDomain)

fun StableDiffusionSamplerRaw.mapToDomain(): StableDiffusionSamplerDomain = with(this) {
    StableDiffusionSamplerDomain(
        name = name,
        aliases = aliases,
        options = options,
    )
}
//endregion

//region DOMAIN -> ENTITY
fun List<StableDiffusionSamplerDomain>.mapToEntity(): List<StableDiffusionSamplerEntity> =
    map(StableDiffusionSamplerDomain::mapToEntity)

fun StableDiffusionSamplerDomain.mapToEntity(): StableDiffusionSamplerEntity = with(this) {
    StableDiffusionSamplerEntity(
        id = name,
        name = name,
        aliases = aliases,
        options = options,
    )
}
//endregion
