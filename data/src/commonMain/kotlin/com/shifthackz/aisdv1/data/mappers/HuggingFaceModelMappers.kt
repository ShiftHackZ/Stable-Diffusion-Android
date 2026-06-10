package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<HuggingFaceModel>.mapDomainToEntity(): List<HuggingFaceModelEntity> =
    map(HuggingFaceModel::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
fun HuggingFaceModel.mapDomainToEntity(): HuggingFaceModelEntity = with(this) {
    HuggingFaceModelEntity(
        id = id,
        name = name,
        alias = alias,
        source = source,
    )
}

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<HuggingFaceModelEntity>.mapEntityToDomain(): List<HuggingFaceModel> =
    map(HuggingFaceModelEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun HuggingFaceModelEntity.mapEntityToDomain(): HuggingFaceModel = with(this) {
    HuggingFaceModel(
        id = id,
        name = name,
        alias = alias,
        source = source,
    )
}
