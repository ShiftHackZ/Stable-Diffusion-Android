package com.shifthackz.aisdv1.domain.entity

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

    companion object {
        fun parse(value: String) = entries.find { "$it" == value } ?: MAUVE
    }
}
