package com.shifthackz.aisdv1.core.validation.url

import com.shifthackz.aisdv1.core.validation.ValidationResult

interface UrlValidator {

    operator fun invoke(input: String?): ValidationResult<Error>

    sealed interface Error {
        data object Empty : Error
        data object BadScheme : Error
        data object BadPort : Error
        data object Invalid : Error
        data object Localhost : Error
    }
}
