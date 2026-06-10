package com.shifthackz.aisdv1.feature.auth.credentials

/**
 * Defines the `Credentials` contract for the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
internal interface Credentials {
    /**
     * Converts SDAI data with `toJson`.
     *
     * @return Result produced by `toJson`.
     * @author Dmitriy Moroz
     */
    fun toJson(): String
}
