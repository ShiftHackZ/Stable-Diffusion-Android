package com.shifthackz.aisdv1.storage.db.cache.contract

/**
 * Defines the Room table and columns for cached ArliAI checkpoints.
 *
 * @author Dmitriy Moroz
 */
internal object ArliAiModelContract {
    /**
     * Room table containing ArliAI checkpoint metadata.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "arli_ai_models"

    /**
     * Stable checkpoint identifier used as the primary key.
     *
     * @author Dmitriy Moroz
     */
    const val ID = "id"
    /**
     * Provider-facing checkpoint title.
     *
     * @author Dmitriy Moroz
     */
    const val TITLE = "title"
    /**
     * Provider-facing checkpoint model name.
     *
     * @author Dmitriy Moroz
     */
    const val NAME = "name"
    /**
     * Short provider hash when ArliAI returns one.
     *
     * @author Dmitriy Moroz
     */
    const val HASH = "hash"
    /**
     * Full SHA-256 model hash when ArliAI returns one.
     *
     * @author Dmitriy Moroz
     */
    const val SHA256 = "sha256"
    /**
     * Provider checkpoint filename.
     *
     * @author Dmitriy Moroz
     */
    const val FILENAME = "filename"
    /**
     * Provider checkpoint config filename.
     *
     * @author Dmitriy Moroz
     */
    const val CONFIG = "config"
}
