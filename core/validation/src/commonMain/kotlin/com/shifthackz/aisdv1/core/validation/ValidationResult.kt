@file:Suppress("unused")

package com.shifthackz.aisdv1.core.validation

/**
 * Carries `ValidationResult` data through the SDAI validation layer.
 *
 * @author Dmitriy Moroz
 */
data class ValidationResult<T : Any>(
    /**
     * Exposes the `isValid` value used by the SDAI validation layer.
     *
     * @author Dmitriy Moroz
     */
    val isValid: Boolean,
    /**
     * Exposes the `validationError` value used by the SDAI validation layer.
     *
     * @author Dmitriy Moroz
     */
    val validationError: T? = null,
) {
    val isNotValid: Boolean
        get() = !isValid
}
