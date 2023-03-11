package com.shifthackz.aisdv1.core.validation

data class ValidationResult<T : Any>(
    val isValid: Boolean,
    val validationError: T? = null,
) {
    val isNotValid: Boolean
        get() = !isValid
}
