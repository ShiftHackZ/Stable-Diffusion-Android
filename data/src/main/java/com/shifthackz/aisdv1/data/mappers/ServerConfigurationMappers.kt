package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ServerConfigurationDomain
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw
import com.shifthackz.aisdv1.storage.database.entity.ServerConfigurationEntity

//region RAW --> DOMAIN
fun ServerConfigurationRaw.mapToDomain(): ServerConfigurationDomain = with(this) {
    ServerConfigurationDomain(
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion

//region DOMAIN --> RAW
fun ServerConfigurationDomain.mapToRequest(): ServerConfigurationRaw = with(this) {
    ServerConfigurationRaw(
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion

//region DOMAIN --> ENTITY
fun ServerConfigurationDomain.mapToEntity(): ServerConfigurationEntity = with(this) {
    ServerConfigurationEntity(
        serverId = "server0",
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion

//region ENTITY --> DOMAIN
fun ServerConfigurationEntity.mapToDomain(): ServerConfigurationDomain = with(this) {
    ServerConfigurationDomain(
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion
