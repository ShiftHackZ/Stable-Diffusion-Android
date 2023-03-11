package com.shifthackz.aisdv1.core.validation.dimension

import com.shifthackz.aisdv1.core.validation.ValidationResult

fun interface DimensionValidator {

    operator fun invoke(input: String?): ValidationResult<Error>

    sealed interface Error {
        object Empty : Error
        object Unexpected : Error
        data class LessThanMinimum(val min: Int) : Error
        data class BiggerThanMaximum(val max: Int) : Error
    }
}
