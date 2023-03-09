package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class SettingsViewModel(
    getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<SettingsState, EmptyEffect>() {

    override val emptyState = SettingsState.Uninitialized

    init {
        !getStableDiffusionModelsUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    t.printStackTrace()
                },
                onSuccess = { data ->
                    setState(
                        SettingsState.Content(
                            sdModels = data.map { (model, _) -> model.title },
                            sdModelSelected = data.firstOrNull { it.second }?.first?.title ?: "",
                        )
                    )
                },
            )
    }

    fun selectStableDiffusionModel(value: String) = (currentState as? SettingsState.Content)
        ?.copy(sdModelSelected = value)
        ?.also(::setState)
        ?.let(SettingsState.Content::sdModelSelected)
        ?.let(selectStableDiffusionModelUseCase::invoke)
        ?.subscribeOnMainThread(schedulersProvider)
        ?.subscribeBy(
            onError = {

            },
            onComplete = {

            },
        )
        ?.addToDisposable()
}
