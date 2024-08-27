package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.network.response.DownloadableModelResponse
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity

//region RAW --> DOMAIN
fun List<DownloadableModelResponse>.mapRawToCheckpointDomain(
    type: LocalAiModel.Type,
): List<LocalAiModel> = map { it.mapRawToCheckpointDomain(type) }

fun DownloadableModelResponse.mapRawToCheckpointDomain(
    type: LocalAiModel.Type,
): LocalAiModel = with(this) {
    LocalAiModel(
        id = id ?: "",
        type = type,
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
    LocalModelEntity(id, type.key, name, size, sources)
}
//endregion

//region ENTITY --> DOMAIN
fun List<LocalModelEntity>.mapEntityToDomain(): List<LocalAiModel> =
    map(LocalModelEntity::mapEntityToDomain)

fun LocalModelEntity.mapEntityToDomain(): LocalAiModel = with(this) {
    LocalAiModel(id, LocalAiModel.Type.parse(type), name, size, sources)
}
//endregion
