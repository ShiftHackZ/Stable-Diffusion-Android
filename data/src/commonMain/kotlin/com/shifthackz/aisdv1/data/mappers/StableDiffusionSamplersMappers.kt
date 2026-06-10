package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity

//region DOMAIN --> ENTITY
/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionSampler>.mapDomainToEntity(): List<StableDiffusionSamplerEntity> =
    map(StableDiffusionSampler::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
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
/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionSamplerEntity>.mapEntityToDomain(): List<StableDiffusionSampler> =
    map(StableDiffusionSamplerEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun StableDiffusionSamplerEntity.mapEntityToDomain(): StableDiffusionSampler = with(this) {
    StableDiffusionSampler(
        name = name,
        aliases = aliases,
        options = options
    )
}
//endregion
