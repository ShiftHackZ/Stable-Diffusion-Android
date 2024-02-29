package com.shifthackz.aisdv1.domain.entity

enum class ServerSource(val key: String) {
    AUTOMATIC1111("custom"),
    HORDE("horde"),
    HUGGING_FACE("hugging_face"),
    LOCAL("local");

    companion object {
        fun parse(value: String) = entries.find { it.key == value } ?: AUTOMATIC1111
    }
}
