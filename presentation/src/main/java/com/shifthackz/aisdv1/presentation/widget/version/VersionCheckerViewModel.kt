package com.shifthackz.aisdv1.presentation.widget.version

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.version.CheckAppVersionUpdateUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class VersionCheckerViewModel(
    private val checkAppVersionUpdateUseCase: CheckAppVersionUpdateUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<VersionCheckerState, EmptyEffect>() {

    override val emptyState = VersionCheckerState.Idle

    init {
        checkForUpdate()
    }

    fun checkForUpdate(notifyIfSame: Boolean = false) = !checkAppVersionUpdateUseCase()
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { result ->
            val state = when (result) {
                is CheckAppVersionUpdateUseCase.Result.NewVersionAvailable -> {
                    VersionCheckerState.UpdatePopUp(result)
                }
                CheckAppVersionUpdateUseCase.Result.NoUpdateNeeded -> {
                    if (notifyIfSame) VersionCheckerState.UpdatePopUp(result)
                    else VersionCheckerState.Idle
                }
            }
            setState(state)
        }

    fun skipUpdate() = setState(VersionCheckerState.Idle)
}
