package com.shifthackz.aisdv1.presentation.model

/**
 * Coordinates `LaunchSource` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
enum class LaunchSource {
    SPLASH,
    SETTINGS;

    /**
     * Provides the `companion object` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `fromKey` step in the SDAI presentation layer.
         *
         * @param key key value consumed by the API.
         * @author Dmitriy Moroz
         */
        fun fromKey(key: Int) = entries.firstOrNull { it.ordinal == key } ?: SPLASH
    }
}
