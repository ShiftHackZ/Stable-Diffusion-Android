package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `BackgroundWorkStatus` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class BackgroundWorkStatus(
    /**
     * Exposes the `running` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val running: Boolean,
    /**
     * Exposes the `statusTitle` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val statusTitle: String,
    /**
     * Exposes the `statusSubTitle` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val statusSubTitle: String,
)
