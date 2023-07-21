package com.shifthackz.aisdv1.domain.usecase.features

import com.shifthackz.aisdv1.domain.entity.FeatureFlags
import io.reactivex.rxjava3.core.Single

interface GetFeatureFlagsUseCase {
    operator fun invoke(): Single<FeatureFlags>
}
