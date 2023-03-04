package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModelDomain
import com.shifthackz.aisdv1.network.model.StableDiffusionModelRaw
import com.shifthackz.aisdv1.storage.db_cache.entity.StableDiffusionModelEntity

//region RAW --> DOMAIN
fun List<StableDiffusionModelRaw>.mapRawToDomain(): List<StableDiffusionModelDomain> =
    map(StableDiffusionModelRaw::mapRawToDomain)

fun StableDiffusionModelRaw.mapRawToDomain(): StableDiffusionModelDomain = with(this) {
    StableDiffusionModelDomain(
        title = title,
        modelName = modelName,
        hash = hash ?: "",
        sha256 = sha256 ?: "",
        filename = filename,
        config = config ?: "",
    )
}
//endregion

//region DOMAIN --> ENTITY
fun List<StableDiffusionModelDomain>.mapDomainToEntity(): List<StableDiffusionModelEntity> =
    map(StableDiffusionModelDomain::mapDomainToEntity)

fun StableDiffusionModelDomain.mapDomainToEntity(): StableDiffusionModelEntity = with(this) {
    StableDiffusionModelEntity(
        id = "${title}_${hash}".trim(),
        title = title,
        name = modelName,
        hash = hash,
        sha256 = sha256,
        filename = filename,
        config = config,
    )
}
//endregion

//region ENTITY --> DOMAIN
fun List<StableDiffusionModelEntity>.mapEntityToDomain(): List<StableDiffusionModelDomain> =
    map(StableDiffusionModelEntity::mapEntityToDomain)

fun StableDiffusionModelEntity.mapEntityToDomain(): StableDiffusionModelDomain = with(this) {
    StableDiffusionModelDomain(
        title = title,
        modelName = name,
        hash = hash,
        sha256 = sha256,
        filename = filename,
        config = config,
    )
}
//endregion
