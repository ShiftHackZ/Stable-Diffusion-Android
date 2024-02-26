package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import com.shifthackz.aisdv1.network.model.StableDiffusionLoraRaw
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity

//region RAW --> DOMAIN
fun List<StableDiffusionLoraRaw>.mapToDomain(): List<StableDiffusionLora> =
    map(StableDiffusionLoraRaw::mapToDomain)

fun StableDiffusionLoraRaw.mapToDomain(): StableDiffusionLora = with(this) {
    StableDiffusionLora(
        name = name ?: "",
        alias = alias ?: "",
        path = path ?: "",
    )
}
//endregion

//region DOMAIN -> ENTITY
fun List<StableDiffusionLora>.mapDomainToEntity(): List<StableDiffusionLoraEntity> =
    map(StableDiffusionLora::mapDomainToEntity)

fun StableDiffusionLora.mapDomainToEntity(): StableDiffusionLoraEntity = with(this) {
    StableDiffusionLoraEntity(
        id = name,
        name = name,
        alias = alias,
        path = path,
    )
}
//endregion

//region ENTITY -> DOMAIN
fun List<StableDiffusionLoraEntity>.mapEntityToDomain(): List<StableDiffusionLora> =
    map(StableDiffusionLoraEntity::mapEntityToDomain)

fun StableDiffusionLoraEntity.mapEntityToDomain(): StableDiffusionLora = with(this) {
    StableDiffusionLora(
        name = name,
        alias = alias,
        path = path,
    )
}
//endregion
