package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `ServerConfiguration` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class ServerConfiguration(
    /**
     * Exposes the `sdModelCheckpoint` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val sdModelCheckpoint: String,
)
