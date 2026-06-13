package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.storage.db.cache.entity.ArliAiModelEntity

/**
 * Converts SDAI data with `mapDomainToArliAiEntity`.
 *
 * @return Result produced by `mapDomainToArliAiEntity`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionModel>.mapDomainToArliAiEntity(): List<ArliAiModelEntity> =
    distinctBy(StableDiffusionModel::arliAiCheckpointName)
        .map(StableDiffusionModel::mapDomainToArliAiEntity)

/**
 * Converts SDAI data with `mapDomainToArliAiEntity`.
 *
 * @author Dmitriy Moroz
 */
fun StableDiffusionModel.mapDomainToArliAiEntity(): ArliAiModelEntity = with(this) {
    ArliAiModelEntity(
        id = arliAiCheckpointName,
        title = title,
        name = modelName,
        hash = hash,
        sha256 = sha256,
        filename = filename,
        config = config,
    )
}

/**
 * Converts SDAI data with `mapArliAiEntityToDomain`.
 *
 * @return Result produced by `mapArliAiEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<ArliAiModelEntity>.mapArliAiEntityToDomain(): List<StableDiffusionModel> =
    map(ArliAiModelEntity::mapArliAiEntityToDomain)

/**
 * Converts SDAI data with `mapArliAiEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun ArliAiModelEntity.mapArliAiEntityToDomain(): StableDiffusionModel = with(this) {
    StableDiffusionModel(
        title = title,
        modelName = name,
        hash = hash,
        sha256 = sha256,
        filename = filename,
        config = config,
    )
}

/**
 * Exposes the `StableDiffusionModel` value used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
private val StableDiffusionModel.arliAiCheckpointName: String
    get() = title.ifBlank { modelName }.ifBlank { filename }
