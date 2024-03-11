package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import io.reactivex.rxjava3.core.Single

interface StabilityAiEnginesRepository {
    fun fetchAndGet(): Single<List<StabilityAiEngine>>
}
