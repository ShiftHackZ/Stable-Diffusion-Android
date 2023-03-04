package com.shifthackz.aisdv1.storage.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConverters {

    @TypeConverter
    fun mapToString(value: Map<String, String>): String = Gson().toJson(value)

    @TypeConverter
    fun stringToMap(value: String): Map<String, String> = Gson().fromJson(
        value,
        object : TypeToken<Map<String, String>>() {}.type,
    )
}
