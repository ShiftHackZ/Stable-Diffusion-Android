package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `StabilityAiEngine` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class StabilityAiEngine(
    /**
     * Exposes the `id` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val id: String,
    /**
     * Exposes the `name` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
)
