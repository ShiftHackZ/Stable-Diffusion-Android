package com.shifthackz.aisdv1.storage.db.cache.contract

/**
 * Provides the `SwarmUiModelContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal object SwarmUiModelContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "swarm_models"

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
     * Exposes the `TITLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TITLE = "title"
    /**
     * Exposes the `AUTHOR` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val AUTHOR = "author"
}
