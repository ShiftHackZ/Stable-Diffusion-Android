package com.shifthackz.aisdv1.presentation.utils

import com.shifthackz.aisdv1.presentation.model.ExtraType

object ExtrasFormatter {

    fun isExtraPresentInPrompt(
        prompt: String,
        loraAlias: String,
        type: ExtraType = ExtraType.Lora,
    ): Boolean {
        val keywords = prompt.split(",", " ")
            .map(String::trim)
            .filterNot(String::isBlank)
        return keywords.any { it.startsWith("<${type.raw}:$loraAlias:") }
    }

    fun isExtraWithValuePresentInPrompt(
        prompt: String,
        loraAlias: String,
        type: ExtraType = ExtraType.Lora,
    ): Pair<Boolean, String?> {
        val keywords = prompt.split(",", " ")
            .map(String::trim)
            .filterNot(String::isBlank)
        val index = keywords.indexOfFirst { it.startsWith("<${type.raw}:$loraAlias:") }
        if (index == -1) return false to null
        return true to keywords[index]
            .replace("<", "")
            .replace(">", "")
            .split(":")
            .lastOrNull()
    }

    fun toggleExtraPromptAlias(
        prompt: String,
        loraAlias: String,
        type: ExtraType = ExtraType.Lora,
    ): String {
        val keywords = prompt.split(",", " ")
            .map(String::trim)
            .filterNot(String::isBlank)
        println(keywords)
        val index = keywords.indexOfFirst { it.startsWith("<${type.raw}:$loraAlias:") }
        if (index == -1) {
            return "${prompt.trim()} <${type.raw}:$loraAlias:1>".trim()
        }
        return prompt.replaceFirst(keywords[index], "").trim()
    }
}
