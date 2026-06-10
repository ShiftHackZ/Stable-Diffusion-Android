package com.shifthackz.aisdv1.storage.converters

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Coordinates `MapConverters` behavior in the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal class MapConverters {

    /**
     * Converts SDAI data with `mapToString`.
     *
     * @param value value value consumed by the API.
     * @return Result produced by `mapToString`.
     * @author Dmitriy Moroz
     */
    @TypeConverter
    fun mapToString(value: Map<String, String>): String =
        json.encodeToString(serializer, value)

    /**
     * Converts SDAI data with `stringToMap`.
     *
     * @param value value value consumed by the API.
     * @return Result produced by `stringToMap`.
     * @author Dmitriy Moroz
     */
    @TypeConverter
    fun stringToMap(value: String): Map<String, String> =
        json.decodeFromString(serializer, value)

    /**
     * Provides the `companion object` singleton used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `serializer` value used by the SDAI storage layer.
         *
         * @author Dmitriy Moroz
         */
        val serializer = MapSerializer(String.serializer(), String.serializer())
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
