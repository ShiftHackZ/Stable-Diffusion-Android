package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionModelEntity

//region DOMAIN --> ENTITY
/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionModel>.mapDomainToEntity(): List<StableDiffusionModelEntity> =
    map(StableDiffusionModel::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
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
/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionModelEntity>.mapEntityToDomain(): List<StableDiffusionModel> =
    map(StableDiffusionModelEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
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
