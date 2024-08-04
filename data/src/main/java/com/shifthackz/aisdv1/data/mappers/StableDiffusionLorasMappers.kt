package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.network.model.StableDiffusionLoraRaw
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity

//region RAW --> DOMAIN
fun List<StableDiffusionLoraRaw>.mapToDomain(): List<LoRA> =
    map(StableDiffusionLoraRaw::mapToDomain)

fun StableDiffusionLoraRaw.mapToDomain(): LoRA = with(this) {
    LoRA(
        name = name ?: "",
        alias = alias ?: "",
        path = path ?: "",
    )
}
//endregion

//region DOMAIN -> ENTITY
fun List<LoRA>.mapDomainToEntity(): List<StableDiffusionLoraEntity> =
    map(LoRA::mapDomainToEntity)

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
fun List<StableDiffusionLoraEntity>.mapEntityToDomain(): List<LoRA> =
    map(StableDiffusionLoraEntity::mapEntityToDomain)

fun StableDiffusionLoraEntity.mapEntityToDomain(): LoRA = with(this) {
    LoRA(
        name = name,
        alias = alias,
        path = path,
    )
}
//endregion
