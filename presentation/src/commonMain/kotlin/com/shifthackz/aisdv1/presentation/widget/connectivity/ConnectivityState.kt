package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Defines the `ConnectivityState` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ConnectivityState : MviState {

    /**
     * Exposes the `enabled` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val enabled: Boolean

    /**
     * Carries `Uninitialized` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class Uninitialized(override val enabled: Boolean = true) : ConnectivityState
    /**
     * Carries `Connected` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class Connected(override val enabled: Boolean = true) : ConnectivityState
    /**
     * Carries `Disconnected` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class Disconnected(override val enabled: Boolean = true) : ConnectivityState

    /**
     * Provides the `companion object` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `consume` step in the SDAI presentation layer.
         *
         * @param value value value consumed by the API.
         * @return Result produced by `consume`.
         * @author Dmitriy Moroz
         */
        fun consume(value: Pair<Boolean, Boolean>): ConnectivityState {
            val (connected, enabled) = value
            return if (connected) Connected(enabled) else Disconnected(enabled)
        }
    }
}
