package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw

//region RAW --> DOMAIN
fun List<StabilityAiEngineRaw>.mapRawToCheckpointDomain(): List<StabilityAiEngine> =
    map(StabilityAiEngineRaw::mapRawToCheckpointDomain)

fun StabilityAiEngineRaw.mapRawToCheckpointDomain(): StabilityAiEngine = with(this) {
    StabilityAiEngine(id ?: "", name ?: "")
}
//endregion
