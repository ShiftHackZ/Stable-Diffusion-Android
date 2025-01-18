@file:Suppress("unused")

package com.shifthackz.aisdv1.presentation.utils

import com.shifthackz.aisdv1.presentation.model.ExtraType

object ExtrasFormatter {

    /**
     * Determines the type of extra based on the input string.
     *
     * This function checks if the input string matches the format of any [ExtraType].
     * The format is expected to be "<TYPE:...>" where TYPE is the raw value of an [ExtraType].
     *
     * @param input The string to be checked. Can be null.
     *
     * @return The matching [ExtraType] if found, or null if the input is null, blank, or doesn't match any type.
     */
    fun determineExtraType(input: String?): ExtraType? {
        if (input.isNullOrBlank()) return null
        ExtraType.entries.forEach { type ->
            if (input.startsWith("<${type.raw}:") && input.endsWith(">")) {
                return type
            }
        }
        return null
    }

    /**
     * Checks if a specific embedding is present in the given prompt.
     *
     * This function splits the prompt into individual keywords and checks if the specified embedding
     * is present among these keywords.
     *
     * @param prompt The input prompt string to search within. It's expected to be a comma or space-separated list of keywords.
     * @param embedding The specific embedding string to search for in the prompt.
     * @return Boolean value indicating whether the embedding is present (true) or not (false) in the prompt.
     */
    fun isEmbeddingPresentInPrompt(
        prompt: String,
        embedding: String,
    ): Boolean {
        val keywords = prompt.split(",", " ")
            .map(String::trim)
            .filterNot(String::isBlank)
        return keywords.contains(embedding)
    }

    /**
     * Checks if a specific extra is present in the given prompt.
     *
     * This function splits the prompt into individual keywords and checks if any keyword
     * starts with the specified extra type and alias.
     *
     * @param prompt The input prompt string to search within. It's expected to be a comma or space-separated list of keywords.
     * @param loraAlias The specific alias of the extra to search for in the prompt.
     * @param type The type of extra to check for. Defaults to [ExtraType.Lora].
     * @return Boolean value indicating whether the extra is present (true) or not (false) in the prompt.
     */
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

    /**
     * Checks if a specific extra with its value is present in the given prompt.
     *
     * This function searches for an extra of the specified type and alias within the prompt,
     * and if found, extracts its value.
     *
     * @param prompt The input prompt string to search within. It's expected to be a comma or space-separated list of keywords.
     * @param loraAlias The specific alias of the extra to search for in the prompt.
     * @param type The type of extra to check for. Defaults to [ExtraType.Lora].
     * 
     * @return A [Pair] where:
     * - The first element is a [Boolean] indicating whether the extra is present (true) or not (false) in the prompt.
     * - The second element is a [String] containing the value of the extra if found, or null if not found.
     */
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

    /**
     * Toggles the presence of a specific extra prompt alias in the given prompt string.
     *
     * If the extra prompt alias is not present in the prompt, it adds it with a default value of 1.
     * If the extra prompt alias is already present, it removes it from the prompt.
     *
     * @param prompt The input prompt string to modify. It's expected to be a comma or space-separated list of keywords.
     * @param loraAlias The specific alias of the extra to toggle in the prompt.
     * @param type The type of extra to toggle. Defaults to [ExtraType.Lora].
     *
     * @return A new string with the extra prompt alias either added or removed, depending on its initial presence.
     */
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
            return if (prompt.isEmpty()) {
                "<${type.raw}:$loraAlias:1>"
            } else {
                "${prompt.trim()}, <${type.raw}:$loraAlias:1>".trim().trim(',')
            }
        }
        return prompt.replaceFirst(keywords[index], "").trim().trim(',')
    }

    /**
     * Toggles the presence of a specific embedding in the given prompt string.
     *
     * If the embedding is not present in the prompt, it adds it.
     * If the embedding is already present, it removes it from the prompt.
     *
     * @param prompt The input prompt string to modify. It's expected to be a comma or space-separated list of keywords.
     * @param embedding The specific embedding to toggle in the prompt.
     *
     * @return A new string with the embedding either added or removed, depending on its initial presence.
     */
    fun toggleEmbedding(
        prompt: String,
        embedding: String,
    ): String {
        val keywords = prompt.split(",", " ")
            .map(String::trim)
            .filterNot(String::isBlank)
        val index = keywords.indexOfFirst { it == embedding }
        if (index == -1) {
            return if (prompt.isEmpty()) {
                embedding
            } else {
                "${prompt.trim()}, $embedding".trim().trim(',')
            }
        }
        return prompt.replaceFirst(keywords[index], "").trim().trim(',')
    }
}
