package com.shifthackz.aisdv1.domain.entity

enum class DarkThemeToken {
    FRAPPE, MACCHIATO, MOCHA;

    companion object {
        fun parse(value: String) = entries.find { "$it" == value } ?: FRAPPE
    }
}
