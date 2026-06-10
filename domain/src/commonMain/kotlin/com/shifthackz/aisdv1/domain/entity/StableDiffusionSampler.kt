package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `StableDiffusionSampler` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class StableDiffusionSampler(
    /**
     * Exposes the `name` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
    /**
     * Exposes the `aliases` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val aliases: List<String>,
    /**
     * Exposes the `options` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val options: Map<String, String>,
)
