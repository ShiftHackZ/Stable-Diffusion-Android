package com.shifthackz.aisdv1.domain.entity

/**
 * Carries only gallery-grid fields for a generated image.
 *
 * @author Dmitriy Moroz
 */
data class AiGenerationResultPreview(
    /**
     * Exposes the `id` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val id: Long,
    /**
     * Exposes the `image` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val image: String,
    /**
     * Exposes the `hidden` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val hidden: Boolean,
    /**
     * Exposes the `liked` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val liked: Boolean = false,
)
