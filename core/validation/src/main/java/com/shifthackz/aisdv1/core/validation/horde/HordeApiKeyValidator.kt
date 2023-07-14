package com.shifthackz.aisdv1.core.validation.horde

import com.shifthackz.aisdv1.core.validation.ValidationResult

interface HordeApiKeyValidator {

    operator fun invoke(input: String?) : ValidationResult<Error>

    sealed interface Error {
        object Empty : Error
    }
}
