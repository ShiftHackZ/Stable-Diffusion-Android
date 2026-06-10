package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<SwarmUiModel>.mapDomainToEntity(): List<SwarmUiModelEntity> =
    map(SwarmUiModel::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun SwarmUiModel.mapDomainToEntity(): SwarmUiModelEntity =
    SwarmUiModelEntity(
        id = "${name}_${title}",
        name = name,
        title = title,
        author = author,
    )

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<SwarmUiModelEntity>.mapEntityToDomain(): List<SwarmUiModel> =
    map(SwarmUiModelEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun SwarmUiModelEntity.mapEntityToDomain(): SwarmUiModel =
    SwarmUiModel(
        name = name,
        title = title,
        author = author,
    )
