package com.shifthackz.aisdv1.presentation.screen.inpaint

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.subscribeBy

class InPaintViewModel(
    private val stateProducer: InPaintStateProducer,
    private val schedulersProvider: SchedulersProvider,
    private val mainRouter: MainRouter,
) : MviRxViewModel<InPaintState, InPaintIntent, InPaintEffect>() {

    override val initialState = InPaintState()

    init {
        !Flowable.combineLatest(
            stateProducer.observeInPaint(),
            stateProducer.observeBitmap(),
            ::Pair,
        )
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { (model, bmp) ->
                updateState { it.copy(model = model, bitmap = bmp) }
            }
    }

    override fun processIntent(intent: InPaintIntent) {
        when (intent) {
            is InPaintIntent.Draw -> updateState { state ->
                state.copy(
                    model = state.model.copy(
                        paths = buildList {
                            addAll(state.model.paths)
                            add(intent.path to state.size)
                        },
                    ),
                )
            }

            InPaintIntent.NavigateBack -> {
                stateProducer.updateInPaint(currentState.model)
                mainRouter.navigateBack()
            }

            is InPaintIntent.SelectTab -> updateState {
                it.copy(selectedTab = intent.tab)
            }

            is InPaintIntent.ChangeCapSize -> updateState {
                it.copy(size = intent.size)
            }

            InPaintIntent.Action.Undo -> updateState { state ->
                state.copy(
                    model = state.model.copy(
                        paths = state.model.paths.filterIndexed { index, _ ->
                            index != state.model.paths.size - 1
                        },
                    ),
                )
            }

            InPaintIntent.ScreenModal.Dismiss -> processIntent(
                InPaintIntent.ScreenModal.Show(Modal.None)
            )

            is InPaintIntent.ScreenModal.Show -> updateState {
                it.copy(screenModal = intent.modal)
            }

            InPaintIntent.Action.Clear -> updateState { state ->
                state.copy(
                    screenModal = Modal.None,
                    model = state.model.copy(paths = emptyList()),
                )
            }
        }
    }
}
