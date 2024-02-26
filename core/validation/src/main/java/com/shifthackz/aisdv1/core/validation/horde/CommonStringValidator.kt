package com.shifthackz.aisdv1.core.validation.horde

import com.shifthackz.aisdv1.core.validation.ValidationResult

interface CommonStringValidator {

    operator fun invoke(input: String?) : ValidationResult<Error>

    sealed interface Error {
        data object Empty : Error
    }
}
