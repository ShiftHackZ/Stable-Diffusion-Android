package com.shifthackz.aisdv1.storage.converters

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Coordinates `ListConverters` behavior in the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal class ListConverters {

    /**
     * Executes the `listStringToString` step in the SDAI storage layer.
     *
     * @param value value value consumed by the API.
     * @return Result produced by `listStringToString`.
     * @author Dmitriy Moroz
     */
    @TypeConverter
    fun listStringToString(value: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), value)

    /**
     * Executes the `stringToListString` step in the SDAI storage layer.
     *
     * @param value value value consumed by the API.
     * @return Result produced by `stringToListString`.
     * @author Dmitriy Moroz
     */
    @TypeConverter
    fun stringToListString(value: String): List<String> =
        json.decodeFromString(ListSerializer(String.serializer()), value)

    /**
     * Provides the `companion object` singleton used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `json` value used by the SDAI storage layer.
         *
         * @author Dmitriy Moroz
         */
        val json = Json {
            ignoreUnknownKeys = true
        }
    }
}
