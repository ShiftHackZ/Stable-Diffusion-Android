package com.shifthackz.aisdv1.storage.db.cache.contract

/**
 * Provides the `StableDiffusionSamplerContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal object StableDiffusionSamplerContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "sd_samplers"

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
     * Exposes the `ALIASES` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val ALIASES = "aliases"
    /**
     * Exposes the `OPTIONS` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val OPTIONS = "options"
}
