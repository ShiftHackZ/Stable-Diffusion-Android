package com.shifthackz.aisdv1.domain.interactor

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModelDomain
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StableDiffusionModelSelectionInteractor {
    fun getData(): Single<List<Pair<StableDiffusionModelDomain, Boolean>>>
    fun selectModelByName(modelName: String): Completable
}
