package com.shifthackz.aisdv1.storage.db.cache.contract

/**
 * Provides the `StableDiffusionHyperNetworkContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
object StableDiffusionHyperNetworkContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "hyper_networks"

    /**
     * Exposes the `ID` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val ID = "id"
    /**
     * Exposes the `NAME` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val NAME = "name"
    /**
     * Exposes the `PATH` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val PATH = "path"
}
