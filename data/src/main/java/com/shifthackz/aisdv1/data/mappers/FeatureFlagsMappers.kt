package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.FeatureFlags
import com.shifthackz.aisdv1.network.response.FeatureFlagsResponse

fun FeatureFlagsResponse.mapToDomain(): FeatureFlags = with (this) {
    FeatureFlags(adFeatureEnable = adBottomEnable ?: false)
}
