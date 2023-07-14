package com.shifthackz.aisdv1.core.validation.horde

import com.shifthackz.aisdv1.core.validation.ValidationResult

internal class HordeApiKeyValidatorImpl : HordeApiKeyValidator {

    override fun invoke(input: String?): ValidationResult<HordeApiKeyValidator.Error> = when {
        input.isNullOrEmpty() || input.isBlank() -> ValidationResult(
            isValid = false,
            validationError = HordeApiKeyValidator.Error.Empty,
        )
        else -> ValidationResult(isValid = true)
    }
}
