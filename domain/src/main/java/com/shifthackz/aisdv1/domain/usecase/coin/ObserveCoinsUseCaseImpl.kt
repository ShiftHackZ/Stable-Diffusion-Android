package com.shifthackz.aisdv1.domain.usecase.coin

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.CoinRepository
import io.reactivex.rxjava3.core.Flowable

internal class ObserveCoinsUseCaseImpl(
    private val buildInfoProvider: BuildInfoProvider,
    private val preferenceManager: PreferenceManager,
    private val coinRepository: CoinRepository,
) : ObserveCoinsUseCase {

    override fun invoke(): Flowable<ObserveCoinsUseCase.Result> {
        if (buildInfoProvider.buildType == BuildType.FOSS) {
            return Flowable.just(ObserveCoinsUseCase.Result.FeatureNotAvailable)
        }
        val coinsProducer: () -> Flowable<ObserveCoinsUseCase.Result> = {
            coinRepository.observeAvailableCoins()
                .map { coins ->
                    if (preferenceManager.useSdAiCloud) ObserveCoinsUseCase.Result.Coins(coins)
                    else ObserveCoinsUseCase.Result.UsingOwnServer
                }
        }
        val prefsProducer: () -> Flowable<ObserveCoinsUseCase.Result> = {
            preferenceManager
                .observe()
                .map(Settings::useSdAiCloud)
                .flatMap { useSdAiCloud ->
                    if (useSdAiCloud) coinsProducer()
                    else Flowable.just(ObserveCoinsUseCase.Result.UsingOwnServer)
                }
        }
        return Flowable.mergeArray(coinsProducer.invoke(), prefsProducer.invoke())
    }
}
