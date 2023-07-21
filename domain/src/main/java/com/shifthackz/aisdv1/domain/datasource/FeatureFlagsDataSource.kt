package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.FeatureFlags
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface FeatureFlagsDataSource {

    interface Remote {
        fun fetch(): Single<FeatureFlags>
    }

    interface Local {
        fun getIsLoaded(): Single<Boolean>
        fun get(): Single<FeatureFlags>
        fun store(flags: FeatureFlags): Completable
    }
}
