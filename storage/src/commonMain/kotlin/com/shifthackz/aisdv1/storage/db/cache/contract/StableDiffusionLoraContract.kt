package com.shifthackz.aisdv1.storage.db.cache.contract

/**
 * Provides the `StableDiffusionLoraContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
object StableDiffusionLoraContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "loras"

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
     * Exposes the `ALIAS` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val ALIAS = "alias"
    /**
     * Exposes the `PATH` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val PATH = "path"
}
