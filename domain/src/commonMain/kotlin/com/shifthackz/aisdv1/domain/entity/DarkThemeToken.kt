package com.shifthackz.aisdv1.domain.entity

/**
 * Coordinates `DarkThemeToken` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class DarkThemeToken {
    FRAPPE, MACCHIATO, MOCHA;

    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `parse` step in the SDAI domain layer.
         *
         * @param value value value consumed by the API.
         * @author Dmitriy Moroz
         */
        fun parse(value: String) = entries.find { "$it" == value } ?: FRAPPE
    }
}
