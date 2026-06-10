package com.shifthackz.aisdv1.domain.entity

/**
 * Coordinates `ColorToken` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class ColorToken {
    ROSEWATER,
    FLAMINGO,
    PINK,
    MAUVE,
    RED,
    MAROON,
    PEACH,
    YELLOW,
    GREEN,
    TEAL,
    SKY,
    SAPPHIRE,
    BLUE,
    LAVENDER;

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
        fun parse(value: String) = entries.find { "$it" == value } ?: MAUVE
    }
}
