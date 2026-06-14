package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.storage.db.cache.entity.ArliAiModelEntity

/**
 * Converts domain checkpoint metadata into distinct ArliAI cache rows.
 *
 * @return Room entities keyed by the best available ArliAI checkpoint name.
 *
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionModel>.mapDomainToArliAiEntity(): List<ArliAiModelEntity> =
    distinctBy(StableDiffusionModel::arliAiCheckpointName)
        .map(StableDiffusionModel::mapDomainToArliAiEntity)

/**
 * Converts one domain checkpoint into an ArliAI cache row.
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
 * Converts cached ArliAI model rows into domain checkpoint metadata.
 *
 * @return domain models shown by setup and generation screens.
 *
 * @author Dmitriy Moroz
 */
fun List<ArliAiModelEntity>.mapArliAiEntityToDomain(): List<StableDiffusionModel> =
    map(ArliAiModelEntity::mapArliAiEntityToDomain)

/**
 * Converts one cached ArliAI model row into domain checkpoint metadata.
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
 * Returns the stable key used for ArliAI model cache de-duplication.
 *
 * @author Dmitriy Moroz
 */
private val StableDiffusionModel.arliAiCheckpointName: String
    get() = title.ifBlank { modelName }.ifBlank { filename }
