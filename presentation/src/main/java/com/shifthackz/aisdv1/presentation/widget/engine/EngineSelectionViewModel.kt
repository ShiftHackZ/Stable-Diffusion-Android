package com.shifthackz.aisdv1.presentation.widget.engine

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.model.Quadruple
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchAndGetHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.stabilityai.FetchAndGetStabilityAiEnginesUseCase
import com.shifthackz.android.core.mvi.EmptyEffect
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.subscribeBy

class EngineSelectionViewModel(
    private val preferenceManager: PreferenceManager,
    private val schedulersProvider: SchedulersProvider,
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val selectStableDiffusionModelUseCase: SelectStableDiffusionModelUseCase,
    private val getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    fetchAndGetStabilityAiEnginesUseCase: FetchAndGetStabilityAiEnginesUseCase,
    getHuggingFaceModelsUseCase: FetchAndGetHuggingFaceModelsUseCase,
) : MviRxViewModel<EngineSelectionState, EngineSelectionIntent, EmptyEffect>() {

    override val initialState = EngineSelectionState()

    init {
        val configuration = preferenceManager
            .observe()
            .flatMap { getConfigurationUseCase().toFlowable() }

        val a1111Models = getStableDiffusionModelsUseCase()
            .onErrorReturn { emptyList() }
            .toFlowable()

        val huggingFaceModels = getHuggingFaceModelsUseCase()
            .onErrorReturn { emptyList() }
            .toFlowable()

        val stabilityAiEngines = fetchAndGetStabilityAiEnginesUseCase()
            .onErrorReturn { emptyList() }
            .toFlowable()

        !Flowable.combineLatest(
            configuration,
            a1111Models,
            huggingFaceModels,
            stabilityAiEngines,
            ::Quadruple,
        )
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = ::errorLog,
                onComplete = EmptyLambda,
                onNext = { (config, sdModels, hfModels, stEngines) ->
                    updateState { state ->
                        state.copy(
                            loading = false,
                            mode = config.source,
                            sdModels = sdModels.map { it.first.title },
                            selectedSdModel = sdModels.first { it.second }.first.title,
                            hfModels = hfModels.map { it.alias },
                            selectedHfModel = config.huggingFaceModel,
                            stEngines = stEngines.map { it.id },
                            selectedStEngine = config.stabilityAiEngineId,
                        )
                    }
                },
            )
    }

    override fun processIntent(intent: EngineSelectionIntent) {
        when (currentState.mode) {
            ServerSource.AUTOMATIC1111 -> !selectStableDiffusionModelUseCase(intent.value)
                .doOnSubscribe {
                    updateState {
                        it.copy(
                            loading = true,
                            selectedSdModel = intent.value,
                            )
                    }
                }
                .andThen(getStableDiffusionModelsUseCase())
                .subscribeOnMainThread(schedulersProvider)
                .subscribeBy(::errorLog) { sdModels ->
                    updateState { state ->
                        state.copy(
                            loading = false,
                            sdModels = sdModels.map { it.first.title },
                            selectedSdModel = sdModels.first { it.second }.first.title,
                        )
                    }
                }

            ServerSource.HUGGING_FACE -> preferenceManager.huggingFaceModel = intent.value

            ServerSource.STABILITY_AI -> preferenceManager.stabilityAiEngineId = intent.value

            else -> Unit
        }
    }
}
