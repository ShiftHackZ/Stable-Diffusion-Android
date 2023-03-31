package com.shifthackz.aisdv1.storage.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal class ListConverters {

    @TypeConverter
    fun listStringToString(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun stringToListString(value: String): List<String> = Gson().fromJson(
        value,
        object : TypeToken<List<String>>() {}.type,
    )
}
