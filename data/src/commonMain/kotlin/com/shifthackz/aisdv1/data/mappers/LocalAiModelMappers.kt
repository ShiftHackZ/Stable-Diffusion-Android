package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.network.response.DownloadableModelResponse
import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity

//region RAW --> DOMAIN
/**
 * Converts SDAI data with `mapRawToCheckpointDomain`.
 *
 * @param type type value consumed by the API.
 * @author Dmitriy Moroz
 */
fun List<DownloadableModelResponse>.mapRawToCheckpointDomain(
    type: LocalAiModel.Type,
): List<LocalAiModel> = map { it.mapRawToCheckpointDomain(type) }

/**
 * Converts SDAI data with `mapRawToCheckpointDomain`.
 *
 * @param type type value consumed by the API.
 * @author Dmitriy Moroz
 */
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
/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<LocalAiModel>.mapDomainToEntity(): List<LocalModelEntity> =
    map(LocalAiModel::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
fun LocalAiModel.mapDomainToEntity(): LocalModelEntity = with(this) {
    LocalModelEntity(id, type.key, name, size, sources)
}
//endregion

//region ENTITY --> DOMAIN
/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<LocalModelEntity>.mapEntityToDomain(): List<LocalAiModel> =
    map(LocalModelEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun LocalModelEntity.mapEntityToDomain(): LocalAiModel = with(this) {
    LocalAiModel(id, LocalAiModel.Type.parse(type), name, size, sources)
}
//endregion
