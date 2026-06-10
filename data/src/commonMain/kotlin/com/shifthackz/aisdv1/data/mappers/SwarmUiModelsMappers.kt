package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity

fun List<SwarmUiModel>.mapDomainToEntity(): List<SwarmUiModelEntity> =
    map(SwarmUiModel::mapDomainToEntity)

fun SwarmUiModel.mapDomainToEntity(): SwarmUiModelEntity =
    SwarmUiModelEntity(
        id = "${name}_${title}",
        name = name,
        title = title,
        author = author,
    )

fun List<SwarmUiModelEntity>.mapEntityToDomain(): List<SwarmUiModel> =
    map(SwarmUiModelEntity::mapEntityToDomain)

fun SwarmUiModelEntity.mapEntityToDomain(): SwarmUiModel =
    SwarmUiModel(
        name = name,
        title = title,
        author = author,
    )
