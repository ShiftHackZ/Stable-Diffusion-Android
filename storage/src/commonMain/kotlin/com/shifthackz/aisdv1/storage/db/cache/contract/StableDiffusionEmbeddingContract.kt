package com.shifthackz.aisdv1.storage.db.cache.contract

/**
 * Provides the `StableDiffusionEmbeddingContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
object StableDiffusionEmbeddingContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "embeddings"

    /**
     * Exposes the `ID` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val ID = "id"
    /**
     * Exposes the `KEYWORD` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val KEYWORD = "keyword"
}
