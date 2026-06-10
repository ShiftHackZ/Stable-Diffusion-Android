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

abstract class BaseMviViewModel<S : MviState, I : MviIntent, E : MviEffect>(
    initialState: S,
    private val effectDispatcher: CoroutineDispatcher,
) : ViewModel(), MviViewModel<S, I, E> {

    private val _state = MutableStateFlow(initialState)
    override val state = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    override val effect = _effect.receiveAsFlow()

    protected val currentState: S
        get() = state.value

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

    protected fun updateState(mutation: (S) -> S) {
        _state.update(mutation)
    }

    protected fun emitState(state: S) {
        _state.value = state
    }

    protected fun emitEffect(effect: E) {
        viewModelScope.launch(effectDispatcher) {
            _effect.send(effect)
        }
    }
}
