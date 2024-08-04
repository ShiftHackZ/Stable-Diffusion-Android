package com.shifthackz.aisdv1.presentation.screen.home

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.android.core.mvi.EmptyIntent
import com.shifthackz.android.core.mvi.EmptyState
import io.reactivex.rxjava3.kotlin.subscribeBy

class HomeNavigationViewModel(
    generationFormUpdateEvent: GenerationFormUpdateEvent,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmptyState, EmptyIntent, HomeNavigationEffect>() {

    override val initialState: EmptyState = EmptyState

    init {
        !generationFormUpdateEvent
            .observeRoute()
            .map(AiGenerationResult.Type::mapToRoute)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { route ->
                emitEffect(HomeNavigationEffect(route))
            }
    }
}
