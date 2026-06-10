package com.shifthackz.aisdv1.storage.db.persistent.contract

/**
 * Provides the `HuggingFaceModelContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
object HuggingFaceModelContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "hugging_face_models"

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
     * Exposes the `SOURCE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SOURCE = "source"
}
