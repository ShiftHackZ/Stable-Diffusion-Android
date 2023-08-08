package com.shifthackz.aisdv1.domain.entity

enum class ServerSource(val key: String) {
    CUSTOM("custom"),
    SDAI("sdai"),
    HORDE("horde"),
    LOCAL("local");

    companion object {
        fun parse(value: String) = values().find { it.key == value } ?: CUSTOM
    }
}
