@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.shifthackz.aisdv1.core.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.ui.BuildConfig
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<S : MviState, E : MviEffect> : ViewModel() {

    private val mutableState: MutableStateFlow<S> by lazy { MutableStateFlow(emptyState) }
    private val effectChannel: Channel<E> = Channel()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    protected val currentState: S
        get() = state.value

    val state: StateFlow<S> by lazy { mutableState.asStateFlow() }
    val effectStream: Flow<E> = effectChannel.receiveAsFlow()

    abstract val emptyState: S

    fun setState(state: S) {
        if (BuildConfig.DEBUG) {
            Log.d(this::class.simpleName, "NEW STATE : $state")
        }
        mutableState.tryEmit(state)
    }

    open fun updateState(mutation: (S) -> S) = mutableState.update {
        if (BuildConfig.DEBUG) debugLog("NEW STATE : $state")
        mutation(it)
    }

    fun emitEffect(effect: E) {
        viewModelScope.launch(Dispatchers.Main.immediate + exceptionHandler) {
            effectChannel.send(effect)
        }
    }
}
