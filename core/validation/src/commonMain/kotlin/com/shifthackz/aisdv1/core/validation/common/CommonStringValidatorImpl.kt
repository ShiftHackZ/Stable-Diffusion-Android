package com.shifthackz.aisdv1.core.validation.common

import com.shifthackz.aisdv1.core.validation.ValidationResult

/**
 * Implements `CommonStringValidator` behavior in the SDAI validation layer.
 *
 * @author Dmitriy Moroz
 */
internal class CommonStringValidatorImpl : CommonStringValidator {

    /**
     * Executes the `invoke` step in the SDAI validation layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override fun invoke(input: String?): ValidationResult<CommonStringValidator.Error> = when {
        input.isNullOrEmpty() || input.isBlank() -> ValidationResult(
            isValid = false,
            validationError = CommonStringValidator.Error.Empty,
        )
        else -> ValidationResult(isValid = true)
    }
}
