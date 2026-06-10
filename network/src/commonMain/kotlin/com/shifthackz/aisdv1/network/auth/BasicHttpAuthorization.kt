package com.shifthackz.aisdv1.network.auth

/**
 * Carries `BasicHttpAuthorization` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
data class BasicHttpAuthorization(
    /**
     * Exposes the `login` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val login: String,
    /**
     * Exposes the `password` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val password: String,
)
