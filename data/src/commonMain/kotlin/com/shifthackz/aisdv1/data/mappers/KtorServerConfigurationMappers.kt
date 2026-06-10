package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw

fun ServerConfigurationRaw.mapKtorRawToDomain(): ServerConfiguration =
    ServerConfiguration(
        sdModelCheckpoint = sdModelCheckpoint ?: "Unknown",
    )

fun ServerConfiguration.mapToKtorRequest(): ServerConfigurationRaw =
    ServerConfigurationRaw(
        sdModelCheckpoint = sdModelCheckpoint,
    )
