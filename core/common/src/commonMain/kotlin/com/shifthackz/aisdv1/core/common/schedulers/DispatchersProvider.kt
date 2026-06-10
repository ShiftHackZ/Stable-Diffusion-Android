package com.shifthackz.aisdv1.core.common.schedulers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Defines the `DispatchersProvider` contract for the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
interface DispatchersProvider {
    /**
     * Exposes the `io` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val io: CoroutineDispatcher
    /**
     * Exposes the `ui` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val ui: CoroutineDispatcher
    /**
     * Exposes the `immediate` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val immediate: CoroutineDispatcher
}
