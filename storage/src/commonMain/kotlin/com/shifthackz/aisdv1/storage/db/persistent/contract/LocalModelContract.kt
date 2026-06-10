package com.shifthackz.aisdv1.storage.db.persistent.contract

/**
 * Provides the `LocalModelContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
object LocalModelContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "local_models"

    /**
     * Exposes the `ID` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val ID = "id"
    /**
     * Exposes the `TYPE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TYPE = "type"
    /**
     * Exposes the `NAME` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val NAME = "name"
    /**
     * Exposes the `SIZE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SIZE = "size"
    /**
     * Exposes the `SOURCES` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SOURCES = "sources"
}
