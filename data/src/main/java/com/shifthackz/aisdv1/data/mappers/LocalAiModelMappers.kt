package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.network.response.DownloadableModelResponse
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity

//region RAW --> DOMAIN
fun List<DownloadableModelResponse>.mapRawToCheckpointDomain(): List<LocalAiModel> =
    map(DownloadableModelResponse::mapRawToCheckpointDomain)

fun DownloadableModelResponse.mapRawToCheckpointDomain(): LocalAiModel = with(this) {
    LocalAiModel(
        id = id ?: "",
        name = name ?: "",
        size = size ?: "",
        sources = sources ?: emptyList(),
    )
}
//endregion

//region DOMAIN --> ENTITY
fun List<LocalAiModel>.mapDomainToEntity(): List<LocalModelEntity> =
    map(LocalAiModel::mapDomainToEntity)

fun LocalAiModel.mapDomainToEntity(): LocalModelEntity = with(this) {
    LocalModelEntity(id, name, size, sources)
}
//endregion

//region ENTITY --> DOMAIN
fun List<LocalModelEntity>.mapEntityToDomain(): List<LocalAiModel> =
    map(LocalModelEntity::mapEntityToDomain)

fun LocalModelEntity.mapEntityToDomain(): LocalAiModel = with(this) {
    LocalAiModel(id, name, size, sources)
}
//endregion
