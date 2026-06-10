package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity

//region DOMAIN -> ENTITY
/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<LoRA>.mapDomainToEntity(): List<StableDiffusionLoraEntity> =
    map(LoRA::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
fun LoRA.mapDomainToEntity(): StableDiffusionLoraEntity = with(this) {
    StableDiffusionLoraEntity(
        id = name,
        name = name,
        alias = alias,
        path = path,
    )
}
//endregion

//region ENTITY -> DOMAIN
/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionLoraEntity>.mapEntityToDomain(): List<LoRA> =
    map(StableDiffusionLoraEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun StableDiffusionLoraEntity.mapEntityToDomain(): LoRA = with(this) {
    LoRA(
        name = name,
        alias = alias,
        path = path,
    )
}
//endregion
