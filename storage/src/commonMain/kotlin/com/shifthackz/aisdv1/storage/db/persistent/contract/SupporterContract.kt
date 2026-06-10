package com.shifthackz.aisdv1.storage.db.persistent.contract

/**
 * Provides the `SupporterContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal object SupporterContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "supporters"

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
     * Exposes the `DATE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val DATE = "date"
    /**
     * Exposes the `MESSAGE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val MESSAGE = "message"
}
