package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity

//region RAW --> DOMAIN
fun ServerConfigurationRaw.mapToDomain(): ServerConfiguration = with(this) {
    ServerConfiguration(
        sdModelCheckpoint = sdModelCheckpoint ?: "Unknown",
    )
}
//endregion

//region DOMAIN --> RAW
fun ServerConfiguration.mapToRequest(): ServerConfigurationRaw = with(this) {
    ServerConfigurationRaw(
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion

//region DOMAIN --> ENTITY
fun ServerConfiguration.mapToEntity(): ServerConfigurationEntity = with(this) {
    ServerConfigurationEntity(
        serverId = "server0",
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion

//region ENTITY --> DOMAIN
fun ServerConfigurationEntity.mapToDomain(): ServerConfiguration = with(this) {
    ServerConfiguration(
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion
