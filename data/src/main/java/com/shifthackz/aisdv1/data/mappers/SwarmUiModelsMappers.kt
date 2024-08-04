package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.network.model.SwarmUiModelRaw
import com.shifthackz.aisdv1.network.response.SwarmUiModelsResponse
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity

//region RAW --> DOMAIN
fun SwarmUiModelsResponse.mapRawToDomain(): List<SwarmUiModel> = with(this) {
    this.files?.mapRawToDomain() ?: emptyList()
}

fun List<SwarmUiModelRaw>.mapRawToDomain(): List<SwarmUiModel> = map(SwarmUiModelRaw::mapRawToDomain)

fun SwarmUiModelRaw.mapRawToDomain(): SwarmUiModel = with(this) {
    SwarmUiModel(
        name = name ?: "",
        title = title ?: "",
        author = author ?: "",
    )
}
//endregion

//region DOMAIN --> ENTITY
fun List<SwarmUiModel>.mapDomainToEntity(): List<SwarmUiModelEntity> = map(SwarmUiModel::mapDomainToEntity)

fun SwarmUiModel.mapDomainToEntity(): SwarmUiModelEntity = with(this) {
    SwarmUiModelEntity(
        id = "${name}_${title}",
        name = name,
        title = title,
        author = author,
    )
}
//endregion

//region ENTITY --> DOMAIN
fun List<SwarmUiModelEntity>.mapEntityToDomain(): List<SwarmUiModel> = map(SwarmUiModelEntity::mapEntityToDomain)

fun SwarmUiModelEntity.mapEntityToDomain(): SwarmUiModel = with(this) {
    SwarmUiModel(
        name = name,
        title = title,
        author = author,
    )
}
//endregion
