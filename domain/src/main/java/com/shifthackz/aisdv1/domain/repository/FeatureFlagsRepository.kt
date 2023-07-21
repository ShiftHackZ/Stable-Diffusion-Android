package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.FeatureFlags
import io.reactivex.rxjava3.core.Single

interface FeatureFlagsRepository {
    fun get(): Single<FeatureFlags>
}
