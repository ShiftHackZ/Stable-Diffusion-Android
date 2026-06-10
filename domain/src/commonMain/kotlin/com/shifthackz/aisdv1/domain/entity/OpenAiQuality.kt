package com.shifthackz.aisdv1.domain.entity

/**
 * Coordinates `OpenAiQuality` behavior in the SDAI domain layer.
 *
 * @param key key value consumed by the API.
 * @author Dmitriy Moroz
 */
enum class OpenAiQuality(val key: String) {
    AUTO("auto"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    /**
     * Converts SDAI data with `toString`.
     *
     * @return Result produced by `toString`.
     * @author Dmitriy Moroz
     */
    override fun toString(): String = key
}
