package com.shifthackz.aisdv1.domain.entity

/**
 * Carries Forge checkpoint module metadata through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class ForgeModule(
    /**
     * Exposes the `name` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
    /**
     * Exposes the `path` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val path: String,
)
