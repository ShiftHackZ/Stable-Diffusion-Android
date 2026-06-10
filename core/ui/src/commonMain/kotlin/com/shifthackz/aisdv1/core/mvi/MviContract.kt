package com.shifthackz.aisdv1.core.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MviState

interface MviIntent

interface MviEffect

interface MviViewModel<S : MviState, I : MviIntent, E : MviEffect> {
    val state: StateFlow<S>
    val effect: Flow<E>

    fun processIntent(intent: I)
}

object EmptyState : MviState

object EmptyIntent : MviIntent

object EmptyEffect : MviEffect
