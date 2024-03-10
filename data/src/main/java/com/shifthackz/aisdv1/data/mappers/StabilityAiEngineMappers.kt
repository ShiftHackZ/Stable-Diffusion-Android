package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw

//region RAW --> DOMAIN
fun List<StabilityAiEngineRaw>.mapRawToDomain(): List<StabilityAiEngine> =
    map(StabilityAiEngineRaw::mapRawToDomain)

fun StabilityAiEngineRaw.mapRawToDomain(): StabilityAiEngine = with(this) {
    StabilityAiEngine(id ?: "", name ?: "")
}
//endregion
