package com.shifthackz.aisdv1.domain.entity

/**
 * Coordinates `OpenAiModel` behavior in the SDAI domain layer.
 *
 * @param alias alias value consumed by the API.
 * @author Dmitriy Moroz
 */
enum class OpenAiModel(val alias: String) {
    GPT_IMAGE_2("gpt-image-2"),
    GPT_IMAGE_1_5("gpt-image-1.5"),
    GPT_IMAGE_1("gpt-image-1"),
    GPT_IMAGE_1_MINI("gpt-image-1-mini");

    /**
     * Converts SDAI data with `toString`.
     *
     * @return Result produced by `toString`.
     * @author Dmitriy Moroz
     */
    override fun toString(): String = alias

    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `default` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val default: OpenAiModel = GPT_IMAGE_2

        /**
         * Executes the `parse` step in the SDAI domain layer.
         *
         * @param value value value consumed by the API.
         * @return Result produced by `parse`.
         * @author Dmitriy Moroz
         */
        fun parse(value: String?): OpenAiModel = entries.find { it.alias == value } ?: default
    }
}
