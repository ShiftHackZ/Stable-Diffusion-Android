package com.shifthackz.aisdv1.domain.entity

enum class OpenAiQuality(val key: String) {
    AUTO("auto"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    override fun toString(): String = key
}
