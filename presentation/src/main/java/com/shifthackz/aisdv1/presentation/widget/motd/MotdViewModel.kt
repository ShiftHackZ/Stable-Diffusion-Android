package com.shifthackz.aisdv1.presentation.widget.motd

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.motd.ObserveMotdUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class MotdViewModel(
    private val preferenceManager: PreferenceManager,
    observeMotdUseCase: ObserveMotdUseCase,
    buildInfoProvider: BuildInfoProvider,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<MotdState, EmptyEffect>() {

    override val emptyState = MotdState.Hidden

    init {
        if (buildInfoProvider.buildType == BuildType.GOOGLE_PLAY) {
            !observeMotdUseCase()
                .map { motd -> motd to preferenceManager.useSdAiCloud }
                .map { (motd, usingSdAi) ->
                    if (!usingSdAi) return@map MotdState.Hidden
                    if (!motd.display || motd.isEmpty) return@map MotdState.Hidden
                    return@map MotdState.Content(motd.title, motd.subTitle)
                }
                .onErrorReturn { MotdState.Hidden }
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(::errorLog, EmptyLambda, ::setState)
        }
    }
}
