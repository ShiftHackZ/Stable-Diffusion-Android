package com.shifthackz.aisdv1.presentation.screen.home

import androidx.lifecycle.viewModelScope
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.RxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeNavigationViewModel(
    generationFormUpdateEvent: GenerationFormUpdateEvent,
    schedulersProvider: SchedulersProvider,
) : RxViewModel() {

    private val routeEffectChannel: Channel<String> = Channel()

    val routeEffectStream: Flow<String> = routeEffectChannel.receiveAsFlow()

    init {
        !generationFormUpdateEvent
            .observeRoute()
            .map(AiGenerationResult.Type::mapToRoute)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { route ->
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    routeEffectChannel.send(route)
                }
            }
    }
}
