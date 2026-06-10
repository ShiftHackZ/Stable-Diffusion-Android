package com.shifthackz.aisdv1.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates `BaseMviViewModel` behavior in the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
abstract class BaseMviViewModel<S : MviState, I : MviIntent, E : MviEffect>(
    initialState: S,
    /**
     * Exposes the `effectDispatcher` value used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    private val effectDispatcher: CoroutineDispatcher,
) : ViewModel(), MviViewModel<S, I, E> {

    /**
     * Exposes the `_state` value used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    private val _state = MutableStateFlow(initialState)
    /**
     * Exposes the `state` value used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    override val state = _state.asStateFlow()

    /**
     * Exposes the `_effect` value used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    private val _effect = Channel<E>(Channel.BUFFERED)
    /**
     * Exposes the `effect` value used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    override val effect = _effect.receiveAsFlow()

    /**
     * Exposes the `currentState` value used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    protected val currentState: S
        get() = state.value

    /**
     * Executes the `launch` step in the SDAI core UI layer.
     *
     * @param dispatcher dispatcher value consumed by the API.
     * @param start start value consumed by the API.
     * @param block block value consumed by the API.
     * @return Result produced by `launch`.
     * @author Dmitriy Moroz
     */
    protected fun launch(
        dispatcher: CoroutineDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        return viewModelScope.launch(dispatcher, start = start) {
            try {
                block()
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
            }
        }
    }

    /**
     * Performs the SDAI side effect handled by `updateState`.
     *
     * @param mutation mutation value consumed by the API.
     * @author Dmitriy Moroz
     */
    protected fun updateState(mutation: (S) -> S) {
        _state.update(mutation)
    }

    /**
     * Executes the `emitState` step in the SDAI core UI layer.
     *
     * @param state state rendered or processed by the component.
     * @author Dmitriy Moroz
     */
    protected fun emitState(state: S) {
        _state.value = state
    }

    /**
     * Executes the `emitEffect` step in the SDAI core UI layer.
     *
     * @param effect effect emitted by the MVI workflow.
     * @author Dmitriy Moroz
     */
    protected fun emitEffect(effect: E) {
        viewModelScope.launch(effectDispatcher) {
            _effect.send(effect)
        }
    }
}
