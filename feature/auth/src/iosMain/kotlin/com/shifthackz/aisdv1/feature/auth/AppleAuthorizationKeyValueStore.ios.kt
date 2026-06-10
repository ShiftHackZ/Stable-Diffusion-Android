package com.shifthackz.aisdv1.feature.auth

import platform.Foundation.NSUserDefaults

/**
 * Coordinates `AppleAuthorizationKeyValueStore` behavior in the SDAI authentication feature layer.
 *
 * @param name name value consumed by the API.
 * @author Dmitriy Moroz
 */
internal class AppleAuthorizationKeyValueStore(
    name: String = KEY_PREFERENCE_AUTHORIZATION,
) : AuthorizationKeyValueStore {

    private val defaults = NSUserDefaults(suiteName = name)
        ?: NSUserDefaults.standardUserDefaults

    override fun getString(key: String): String? =
        defaults.stringForKey(key)

    override fun putString(key: String, value: String) {
        defaults.setObject(value, key)
        defaults.synchronize()
    }

    companion object {
        private const val KEY_PREFERENCE_AUTHORIZATION = "sdai_authorization_preference"
    }
}
