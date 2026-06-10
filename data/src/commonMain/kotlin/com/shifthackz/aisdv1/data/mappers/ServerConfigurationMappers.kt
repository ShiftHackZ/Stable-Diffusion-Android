package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity

//region DOMAIN --> ENTITY
/**
 * Converts SDAI data with `mapToEntity`.
 *
 * @author Dmitriy Moroz
 */
fun ServerConfiguration.mapToEntity(): ServerConfigurationEntity = with(this) {
    ServerConfigurationEntity(
        serverId = "server0",
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion

//region ENTITY --> DOMAIN
/**
 * Converts SDAI data with `mapToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun ServerConfigurationEntity.mapToDomain(): ServerConfiguration = with(this) {
    ServerConfiguration(
        sdModelCheckpoint = sdModelCheckpoint,
    )
}
//endregion
