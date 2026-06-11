package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `StableDiffusionScripts` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class StableDiffusionScripts(
    /**
     * Exposes the `txt2img` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val txt2img: List<String> = emptyList(),
    /**
     * Exposes the `img2img` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val img2img: List<String> = emptyList(),
    /**
     * Exposes the `extensions` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val extensions: List<String> = emptyList(),
) {

    /**
     * Executes the `contains` step in the SDAI domain layer.
     *
     * @param script script value consumed by the API.
     * @return Result produced by `contains`.
     * @author Dmitriy Moroz
     */
    fun contains(script: String): Boolean =
        txt2img.any { it.equals(script, ignoreCase = true) } ||
            img2img.any { it.equals(script, ignoreCase = true) } ||
            extensions.any { it.equals(script, ignoreCase = true) }
}
