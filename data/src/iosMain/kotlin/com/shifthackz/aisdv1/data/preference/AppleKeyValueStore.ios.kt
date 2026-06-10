package com.shifthackz.aisdv1.data.preference

import platform.Foundation.NSUserDefaults

private class NsUserDefaultsKeyValueStore(
    name: String,
) : KeyValueStore {

    private val defaults = NSUserDefaults(suiteName = name)
        ?: NSUserDefaults.standardUserDefaults

    override fun getString(key: String, default: String): String =
        defaults.stringForKey(key) ?: default

    override fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    override fun getBoolean(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) == null) default else defaults.boolForKey(key)

    override fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }

    override fun getInt(key: String, default: Int): Int =
        if (defaults.objectForKey(key) == null) default else defaults.integerForKey(key).toInt()

    override fun putInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), forKey = key)
    }
}

internal actual fun createKeyValueStore(name: String): KeyValueStore =
    NsUserDefaultsKeyValueStore(name)
