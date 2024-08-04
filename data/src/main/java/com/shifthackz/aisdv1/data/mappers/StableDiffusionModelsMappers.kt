package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.network.model.StableDiffusionModelRaw
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionModelEntity

//region RAW --> DOMAIN
fun List<StableDiffusionModelRaw>.mapRawToCheckpointDomain(): List<StableDiffusionModel> =
    map(StableDiffusionModelRaw::mapRawToCheckpointDomain)

fun StableDiffusionModelRaw.mapRawToCheckpointDomain(): StableDiffusionModel = with(this) {
    StableDiffusionModel(
        title = title ?: "",
        modelName = modelName ?: "",
        hash = hash ?: "",
        sha256 = sha256 ?: "",
        filename = filename ?: "",
        config = config ?: "",
    )
}
//endregion

//region DOMAIN --> ENTITY
fun List<StableDiffusionModel>.mapDomainToEntity(): List<StableDiffusionModelEntity> =
    map(StableDiffusionModel::mapDomainToEntity)

fun StableDiffusionModel.mapDomainToEntity(): StableDiffusionModelEntity = with(this) {
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
fun List<StableDiffusionModelEntity>.mapEntityToDomain(): List<StableDiffusionModel> =
    map(StableDiffusionModelEntity::mapEntityToDomain)

fun StableDiffusionModelEntity.mapEntityToDomain(): StableDiffusionModel = with(this) {
    StableDiffusionModel(
        title = title,
        modelName = name,
        hash = hash,
        sha256 = sha256,
        filename = filename,
        config = config,
    )
}
//endregion
