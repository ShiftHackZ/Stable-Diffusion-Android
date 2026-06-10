package com.shifthackz.aisdv1.storage.converters

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

internal class MapConverters {

    @TypeConverter
    fun mapToString(value: Map<String, String>): String =
        json.encodeToString(serializer, value)

    @TypeConverter
    fun stringToMap(value: String): Map<String, String> =
        json.decodeFromString(serializer, value)

    private companion object {
        val serializer = MapSerializer(String.serializer(), String.serializer())
        val json = Json {
            ignoreUnknownKeys = true
        }
    }
}
