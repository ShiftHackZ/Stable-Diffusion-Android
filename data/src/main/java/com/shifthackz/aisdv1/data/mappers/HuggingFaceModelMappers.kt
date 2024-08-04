package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity

//region RAW --> DOMAIN
fun List<HuggingFaceModelRaw>.mapRawToCheckpointDomain(): List<HuggingFaceModel> =
    map(HuggingFaceModelRaw::mapRawToCheckpointDomain)

fun HuggingFaceModelRaw.mapRawToCheckpointDomain(): HuggingFaceModel = with(this) {
    HuggingFaceModel(
        id = id ?: "",
        name = name ?: "",
        alias = alias ?: "",
        source = source ?: ""
    )
}
//endregion

//region DOMAIN -> ENTITY
fun List<HuggingFaceModel>.mapDomainToEntity(): List<HuggingFaceModelEntity> =
    map(HuggingFaceModel::mapDomainToEntity)

fun HuggingFaceModel.mapDomainToEntity(): HuggingFaceModelEntity = with(this) {
    HuggingFaceModelEntity(id, name, alias, source)
}
//endregion

//region ENTITY -> DOMAIN
fun List<HuggingFaceModelEntity>.mapEntityToDomain(): List<HuggingFaceModel> =
    map(HuggingFaceModelEntity::mapEntityToDomain)

fun HuggingFaceModelEntity.mapEntityToDomain(): HuggingFaceModel = with(this) {
    HuggingFaceModel(id, name, alias, source)
}
//endregion
