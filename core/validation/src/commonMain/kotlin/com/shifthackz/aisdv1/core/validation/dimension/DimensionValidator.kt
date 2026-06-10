package com.shifthackz.aisdv1.core.validation.dimension

import com.shifthackz.aisdv1.core.validation.ValidationResult

/**
 * Executes the `function` step in the SDAI validation layer.
 *
 * @author Dmitriy Moroz
 */
fun interface DimensionValidator {

    operator fun invoke(input: String?): ValidationResult<Error>

    sealed interface Error {
        data object Empty : Error
        data object Unexpected : Error
        data class LessThanMinimum(val min: Int) : Error
        data class BiggerThanMaximum(val max: Int) : Error
    }
}
