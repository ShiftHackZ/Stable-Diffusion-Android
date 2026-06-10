package com.shifthackz.aisdv1.core.validation.path

import com.shifthackz.aisdv1.core.validation.ValidationResult

/**
 * Implements `FilePathValidator` behavior in the SDAI validation layer.
 *
 * @author Dmitriy Moroz
 */
class FilePathValidatorImpl : FilePathValidator {

    /**
     * Executes the `invoke` step in the SDAI validation layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override fun invoke(input: String?): ValidationResult<FilePathValidator.Error> = when {
        input.isNullOrBlank() -> ValidationResult(
            isValid = false,
            validationError = FilePathValidator.Error.Empty,
        )
        !isValidFilePath(input) -> ValidationResult(
            isValid = false,
            validationError = FilePathValidator.Error.Invalid,
        )
        else -> ValidationResult(true)
    }

    /**
     * Executes the `isValidFilePath` step in the SDAI validation layer.
     *
     * @param path local path used by the operation.
     * @return Result produced by `isValidFilePath`.
     * @author Dmitriy Moroz
     */
    private fun isValidFilePath(path: String): Boolean {
        val regex = Regex("^(/[^/<>:\"|?*]+)+/?$")
        return regex.matches(path)
    }
}
