package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.network.model.ForgeModuleRaw

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun List<ForgeModuleRaw>.mapKtorRawToCheckpointDomain(): List<ForgeModule> =
    map(ForgeModuleRaw::mapKtorRawToCheckpointDomain)

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun ForgeModuleRaw.mapKtorRawToCheckpointDomain(): ForgeModule =
    ForgeModule(
        name = modelName ?: "",
        path = filename ?: "",
    )
