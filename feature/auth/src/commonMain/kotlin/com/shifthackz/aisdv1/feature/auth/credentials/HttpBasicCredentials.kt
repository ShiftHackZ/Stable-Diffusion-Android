package com.shifthackz.aisdv1.feature.auth.credentials

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Carries `HttpBasicCredentials` data through the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
internal data class HttpBasicCredentials(
    /**
     * Exposes the `login` value used by the SDAI authentication feature layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("login")
    val login: String,
    /**
     * Exposes the `password` value used by the SDAI authentication feature layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("password")
    val password: String,
) : Credentials {

    /**
     * Converts SDAI data with `toJson`.
     *
     * @author Dmitriy Moroz
     */
    override fun toJson(): String = Json.encodeToString(this)

    /**
     * Provides the `companion object` singleton used by the SDAI authentication feature layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `fromJson` step in the SDAI authentication feature layer.
         *
         * @param json json value consumed by the API.
         * @return Result produced by `fromJson`.
         * @author Dmitriy Moroz
         */
        fun fromJson(json: String): HttpBasicCredentials =
            Json.decodeFromString(json)
    }
}
