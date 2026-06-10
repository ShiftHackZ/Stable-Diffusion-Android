package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `StableDiffusionModel` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class StableDiffusionModel(
    /**
     * Exposes the `title` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val title: String,
    /**
     * Exposes the `modelName` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val modelName: String,
    /**
     * Exposes the `hash` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val hash: String,
    /**
     * Exposes the `sha256` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val sha256: String,
    /**
     * Exposes the `filename` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val filename: String,
    /**
     * Exposes the `config` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val config: String,
)
