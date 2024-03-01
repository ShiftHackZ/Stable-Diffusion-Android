package com.shifthackz.aisdv1.domain.entity

enum class OpenAiModel(val alias: String) {
    DALL_E_2("dall-e-2"),
    DALL_E_3("dall-e-3");

    companion object {
        fun parse(value: String?) : OpenAiModel = entries.find { it.alias == value } ?: DALL_E_2
    }
}
