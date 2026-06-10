package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

/**
 * Carries `SupporterRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class SupporterRaw(
    /**
     * Exposes the `id` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val id: Int?,
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String?,
    /**
     * Exposes the `date` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val date: String?,
    /**
     * Exposes the `amount` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val amount: String?,
    /**
     * Exposes the `currency` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val currency: String?,
    /**
     * Exposes the `type` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val type: String?,
    /**
     * Exposes the `message` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val message: String?,
)
