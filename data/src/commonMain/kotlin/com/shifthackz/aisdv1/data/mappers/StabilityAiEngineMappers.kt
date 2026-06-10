package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw

/**
 * Converts SDAI data with `mapRawToCheckpointDomain`.
 *
 * @return Result produced by `mapRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun List<StabilityAiEngineRaw>.mapRawToCheckpointDomain(): List<StabilityAiEngine> =
    map(StabilityAiEngineRaw::mapRawToCheckpointDomain)

/**
 * Converts SDAI data with `mapRawToCheckpointDomain`.
 *
 * @return Result produced by `mapRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun StabilityAiEngineRaw.mapRawToCheckpointDomain(): StabilityAiEngine =
    StabilityAiEngine(id ?: "", name ?: "")
