package com.shifthackz.aisdv1.domain.entity

enum class ServerSource(val key: String) {
    CUSTOM("custom"),
    HORDE("horde"),
    LOCAL("local");

    companion object {
        fun parse(value: String) = entries.find { it.key == value } ?: CUSTOM
    }
}
