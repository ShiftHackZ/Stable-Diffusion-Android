package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `LoRA` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class LoRA(
    /**
     * Exposes the `name` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
    /**
     * Exposes the `alias` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val alias: String,
    /**
     * Exposes the `path` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val path: String,
)
