package com.shifthackz.aisdv1.storage.converters

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

internal class ListConverters {

    @TypeConverter
    fun listStringToString(value: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun stringToListString(value: String): List<String> =
        json.decodeFromString(ListSerializer(String.serializer()), value)

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
        }
    }
}
