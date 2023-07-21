package com.shifthackz.aisdv1.presentation.screen.home

import androidx.lifecycle.viewModelScope
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.usecase.features.GetFeatureFlagsUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.features.HomeNavigationItemClick
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeNavigationViewModel(
    getFeatureFlagsUseCase: GetFeatureFlagsUseCase,
    generationFormUpdateEvent: GenerationFormUpdateEvent,
    schedulersProvider: SchedulersProvider,
    private val analytics: Analytics,
) : MviRxViewModel<HomeNavigationState, EmptyEffect>() {

    override val emptyState = HomeNavigationState()

    private val routeEffectChannel: Channel<String> = Channel()

    val routeEffectStream: Flow<String> = routeEffectChannel.receiveAsFlow()

    init {
        !getFeatureFlagsUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .map { flags -> currentState.copy(bottomAdBanner = flags.adHomeBottomEnable) }
            .subscribeBy(::errorLog, ::setState)

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

    fun logNavItemClickEvent(item: HomeNavigationItem) {
        analytics.logEvent(HomeNavigationItemClick(item))
    }
}
