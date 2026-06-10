package com.shifthackz.aisdv1.domain.entity

enum class OpenAiModel(val alias: String) {
    GPT_IMAGE_2("gpt-image-2"),
    GPT_IMAGE_1_5("gpt-image-1.5"),
    GPT_IMAGE_1("gpt-image-1"),
    GPT_IMAGE_1_MINI("gpt-image-1-mini");

    override fun toString(): String = alias

    companion object {
        val default: OpenAiModel = GPT_IMAGE_2

        fun parse(value: String?): OpenAiModel = entries.find { it.alias == value } ?: default
    }
}
