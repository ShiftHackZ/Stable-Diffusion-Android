package com.shifthackz.aisdv1.core.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines the `MviState` contract for the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
interface MviState

/**
 * Defines the `MviIntent` contract for the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
interface MviIntent

/**
 * Defines the `MviEffect` contract for the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
interface MviEffect

/**
 * Defines the `MviViewModel` contract for the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
interface MviViewModel<S : MviState, I : MviIntent, E : MviEffect> {
    /**
     * Exposes the `state` value used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    val state: StateFlow<S>
    /**
     * Exposes the `effect` value used by the SDAI core UI layer.
     *
     * @author Dmitriy Moroz
     */
    val effect: Flow<E>

    /**
     * Executes the `processIntent` step in the SDAI core UI layer.
     *
     * @param intent intent to process in the MVI workflow.
     * @author Dmitriy Moroz
     */
    fun processIntent(intent: I)
}

/**
 * Provides the `EmptyState` singleton used by the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
object EmptyState : MviState

/**
 * Provides the `EmptyIntent` singleton used by the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
object EmptyIntent : MviIntent

/**
 * Provides the `EmptyEffect` singleton used by the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
object EmptyEffect : MviEffect
