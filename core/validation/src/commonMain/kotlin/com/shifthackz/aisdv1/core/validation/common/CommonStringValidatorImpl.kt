package com.shifthackz.aisdv1.core.validation.common

import com.shifthackz.aisdv1.core.validation.ValidationResult

internal class CommonStringValidatorImpl : CommonStringValidator {

    override fun invoke(input: String?): ValidationResult<CommonStringValidator.Error> = when {
        input.isNullOrEmpty() || input.isBlank() -> ValidationResult(
            isValid = false,
            validationError = CommonStringValidator.Error.Empty,
        )
        else -> ValidationResult(isValid = true)
    }
}
