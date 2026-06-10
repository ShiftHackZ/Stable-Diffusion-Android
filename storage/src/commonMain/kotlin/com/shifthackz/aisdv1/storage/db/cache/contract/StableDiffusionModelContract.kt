package com.shifthackz.aisdv1.storage.db.cache.contract

/**
 * Provides the `StableDiffusionModelContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal object StableDiffusionModelContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "sd_models"

    /**
     * Exposes the `ID` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val ID = "id"
    /**
     * Exposes the `TITLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TITLE = "title"
    /**
     * Exposes the `NAME` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val NAME = "name"
    /**
     * Exposes the `HASH` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val HASH = "hash"
    /**
     * Exposes the `SHA256` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SHA256 = "sha256"
    /**
     * Exposes the `FILENAME` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val FILENAME = "filename"
    /**
     * Exposes the `CONFIG` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val CONFIG = "config"
}
