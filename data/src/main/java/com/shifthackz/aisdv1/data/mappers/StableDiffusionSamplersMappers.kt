package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity

//region RAW --> DOMAIN
fun List<StableDiffusionSamplerRaw>.mapRawToCheckpointDomain(): List<StableDiffusionSampler> =
    map(StableDiffusionSamplerRaw::mapRawToCheckpointDomain)

fun StableDiffusionSamplerRaw.mapRawToCheckpointDomain(): StableDiffusionSampler = with(this) {
    StableDiffusionSampler(
        name = name ?: "",
        aliases = aliases ?: emptyList(),
        options = options ?: mapOf(),
    )
}
//endregion

//region DOMAIN --> ENTITY
fun List<StableDiffusionSampler>.mapDomainToEntity(): List<StableDiffusionSamplerEntity> =
    map(StableDiffusionSampler::mapDomainToEntity)

fun StableDiffusionSampler.mapDomainToEntity(): StableDiffusionSamplerEntity = with(this) {
    StableDiffusionSamplerEntity(
        id = name,
        name = name,
        aliases = aliases,
        options = options,
    )
}
//endregion

//region ENTITY --> DOMAIN
fun List<StableDiffusionSamplerEntity>.mapEntityToDomain(): List<StableDiffusionSampler> =
    map(StableDiffusionSamplerEntity::mapEntityToDomain)

fun StableDiffusionSamplerEntity.mapEntityToDomain(): StableDiffusionSampler = with(this) {
    StableDiffusionSampler(
        name = name,
        aliases = aliases,
        options = options
    )
}
//endregion
