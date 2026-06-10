package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity

fun List<HuggingFaceModel>.mapDomainToEntity(): List<HuggingFaceModelEntity> =
    map(HuggingFaceModel::mapDomainToEntity)

fun HuggingFaceModel.mapDomainToEntity(): HuggingFaceModelEntity = with(this) {
    HuggingFaceModelEntity(
        id = id,
        name = name,
        alias = alias,
        source = source,
    )
}

fun List<HuggingFaceModelEntity>.mapEntityToDomain(): List<HuggingFaceModel> =
    map(HuggingFaceModelEntity::mapEntityToDomain)

fun HuggingFaceModelEntity.mapEntityToDomain(): HuggingFaceModel = with(this) {
    HuggingFaceModel(
        id = id,
        name = name,
        alias = alias,
        source = source,
    )
}
