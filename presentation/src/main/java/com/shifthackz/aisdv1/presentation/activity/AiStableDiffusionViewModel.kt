package com.shifthackz.aisdv1.presentation.activity

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.RxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.coin.EarnRewardedCoinsUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class AiStableDiffusionViewModel(
    private val earnRewardedCoinsUseCase: EarnRewardedCoinsUseCase,
    private val preferenceManager: PreferenceManager,
    private val schedulersProvider: SchedulersProvider,
) : RxViewModel() {

    fun earnRewardedCoins(amount: Int) = !earnRewardedCoinsUseCase(amount)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog, EmptyLambda)

    fun onStoragePermissionsGranted() {
        preferenceManager.saveToMediaStore = true
    }
}
