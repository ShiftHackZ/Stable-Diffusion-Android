package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.domain.datasource.FeatureFlagsDataSource
import com.shifthackz.aisdv1.domain.entity.FeatureFlags
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class FeatureFlagsLocalDataSource : FeatureFlagsDataSource.Local {

    private var featureFlags = FeatureFlags()
    private var fetched = false

    override fun getIsLoaded() = Single.just(fetched)

    override fun get() = Single.just(featureFlags)

    override fun store(flags: FeatureFlags) = Completable.fromAction {
        fetched = true
        featureFlags = flags
    }
}
