@file:Suppress("unused")

package com.shifthackz.aisdv1.presentation.utils

import com.shifthackz.aisdv1.presentation.model.ExtraType

object ExtrasFormatter {

    fun determineExtraType(input: String?) : ExtraType? {
        if (input.isNullOrBlank()) return null
        ExtraType.entries.forEach { type ->
            if (input.startsWith("<${type.raw}:") && input.endsWith(">")) {
                return type
            }
        }
        return null
    }

    fun isEmbeddingPresentInPrompt(
        prompt: String,
        embedding: String,
    ): Boolean {
        val keywords = prompt.split(",", " ")
            .map(String::trim)
            .filterNot(String::isBlank)
        return keywords.contains(embedding)
    }

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
        val index = keywords.indexOfFirst { it.startsWith("<${type.raw}:$loraAlias:") }
        if (index == -1) {
            return "${prompt.trim()}, <${type.raw}:$loraAlias:1>".trim()
        }
        return prompt.replaceFirst(keywords[index], "").trim()
    }

    fun toggleEmbedding(
        prompt: String,
        embedding: String,
    ): String {
        val keywords = prompt.split(",", " ")
            .map(String::trim)
            .filterNot(String::isBlank)
        val index = keywords.indexOfFirst { it == embedding }
        if (index == -1) {
            return "${prompt.trim()}, $embedding".trim()
        }
        return prompt.replaceFirst(keywords[index], "").trim()
    }
}
