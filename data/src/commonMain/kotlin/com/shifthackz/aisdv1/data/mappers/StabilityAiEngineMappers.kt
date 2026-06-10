package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw

fun List<StabilityAiEngineRaw>.mapRawToCheckpointDomain(): List<StabilityAiEngine> =
    map(StabilityAiEngineRaw::mapRawToCheckpointDomain)

fun StabilityAiEngineRaw.mapRawToCheckpointDomain(): StabilityAiEngine =
    StabilityAiEngine(id ?: "", name ?: "")
