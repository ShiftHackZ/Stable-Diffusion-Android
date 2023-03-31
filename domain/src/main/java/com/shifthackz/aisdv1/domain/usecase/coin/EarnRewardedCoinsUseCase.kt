package com.shifthackz.aisdv1.domain.usecase.coin

import io.reactivex.rxjava3.core.Completable

interface EarnRewardedCoinsUseCase {
    operator fun invoke(amount: Int): Completable
}
