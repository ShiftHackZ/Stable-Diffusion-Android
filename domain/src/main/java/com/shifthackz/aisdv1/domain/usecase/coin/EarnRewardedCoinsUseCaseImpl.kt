package com.shifthackz.aisdv1.domain.usecase.coin

import com.shifthackz.aisdv1.domain.repository.CoinRepository

internal class EarnRewardedCoinsUseCaseImpl(
    private val coinRepository: CoinRepository,
) : EarnRewardedCoinsUseCase {

    override fun invoke(amount: Int) = coinRepository.earnCoins(amount)
}
