package com.shifthackz.aisdv1.data.preference

import android.content.Context
import android.content.SharedPreferences
import org.koin.mp.KoinPlatform

private class AndroidPreferencesKeyValueStore(
    private val preferences: SharedPreferences,
) : KeyValueStore {

    override fun getString(key: String, default: String): String =
        preferences.getString(key, default) ?: default

    override fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override fun getBoolean(key: String, default: Boolean): Boolean =
        preferences.getBoolean(key, default)

    override fun putBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    override fun getInt(key: String, default: Int): Int =
        preferences.getInt(key, default)

    override fun putInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }
}

internal actual fun createKeyValueStore(name: String): KeyValueStore {
    val context = KoinPlatform.getKoin().get<Context>()
    return AndroidPreferencesKeyValueStore(
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE),
    )
}
