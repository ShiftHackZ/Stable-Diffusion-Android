package com.shifthackz.aisdv1.core.validation.dimension

import com.shifthackz.aisdv1.core.validation.ValidationResult

internal class DimensionValidatorImpl(
    private val minimum: Int = MINIMUM,
    private val maximum: Int = MAXIMUM,
) : DimensionValidator {

    override operator fun invoke(input: String?): ValidationResult<DimensionValidator.Error> =
        when {
            input == null -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.Empty,
            )
            input.isEmpty() -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.Empty,
            )
            input.toIntOrNull() == null -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.Unexpected,
            )
            input.toInt() < minimum -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.LessThanMinimum(minimum),
            )
            input.toInt() > maximum -> ValidationResult(
                isValid = false,
                validationError = DimensionValidator.Error.BiggerThanMaximum(maximum),
            )
            else -> ValidationResult(isValid = true)
        }

    companion object {
        private const val MINIMUM = 64
        private const val MAXIMUM = 2048
    }
}
