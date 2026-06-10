package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw

/**
 * Converts SDAI data with `mapKtorRawToDomain`.
 *
 * @return Result produced by `mapKtorRawToDomain`.
 * @author Dmitriy Moroz
 */
fun ServerConfigurationRaw.mapKtorRawToDomain(): ServerConfiguration =
    ServerConfiguration(
        sdModelCheckpoint = sdModelCheckpoint ?: "Unknown",
    )

/**
 * Converts SDAI data with `mapToKtorRequest`.
 *
 * @return Result produced by `mapToKtorRequest`.
 * @author Dmitriy Moroz
 */
fun ServerConfiguration.mapToKtorRequest(): ServerConfigurationRaw =
    ServerConfigurationRaw(
        sdModelCheckpoint = sdModelCheckpoint,
    )
