package com.shifthackz.aisdv1.core.validation.url

import com.shifthackz.aisdv1.core.validation.ValidationResult

interface UrlValidator {

    operator fun invoke(input: String?): ValidationResult<Error>

    sealed interface Error {
        object Empty : Error
        object BadScheme : Error
        object Invalid : Error
    }
}
