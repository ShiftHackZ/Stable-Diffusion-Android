package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `LocalDiffusionStatus` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class LocalDiffusionStatus(
    /**
     * Exposes the `current` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val current: Int,
    /**
     * Exposes the `total` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val total: Int,
)
