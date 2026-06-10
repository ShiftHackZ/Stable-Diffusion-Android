package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `HordeProcessStatus` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class HordeProcessStatus(
    /**
     * Exposes the `waitTimeSeconds` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val waitTimeSeconds: Int,
    /**
     * Exposes the `queuePosition` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val queuePosition: Int?,
)
