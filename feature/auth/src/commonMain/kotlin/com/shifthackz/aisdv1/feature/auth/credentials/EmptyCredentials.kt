package com.shifthackz.aisdv1.feature.auth.credentials

/**
 * Coordinates `EmptyCredentials` behavior in the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class EmptyCredentials : Credentials {
    /**
     * Converts SDAI data with `toJson`.
     *
     * @return Result produced by `toJson`.
     * @author Dmitriy Moroz
     */
    override fun toJson(): String = ""
}
