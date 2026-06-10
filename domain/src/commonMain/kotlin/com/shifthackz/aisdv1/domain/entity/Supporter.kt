package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `Supporter` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class Supporter(
    /**
     * Exposes the `id` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val id: Int,
    /**
     * Exposes the `name` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
    /**
     * Exposes the `date` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val date: String,
    /**
     * Exposes the `message` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val message: String,
)
