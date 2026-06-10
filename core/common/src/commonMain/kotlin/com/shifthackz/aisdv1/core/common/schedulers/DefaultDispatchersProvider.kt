package com.shifthackz.aisdv1.core.common.schedulers

import kotlinx.coroutines.Dispatchers

/**
 * Provides the `DefaultDispatchersProvider` singleton used by the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
object DefaultDispatchersProvider : DispatchersProvider {
    /**
     * Exposes the `io` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val io = Dispatchers.Default
    /**
     * Exposes the `ui` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val ui = Dispatchers.Main
    /**
     * Exposes the `immediate` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val immediate = Dispatchers.Main.immediate
}
