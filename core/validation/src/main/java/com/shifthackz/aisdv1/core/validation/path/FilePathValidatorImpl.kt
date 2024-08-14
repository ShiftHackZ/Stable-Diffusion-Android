package com.shifthackz.aisdv1.core.validation.path

import com.shifthackz.aisdv1.core.validation.ValidationResult

class FilePathValidatorImpl : FilePathValidator {

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

    private fun isValidFilePath(path: String): Boolean {
        val regex = Regex("^(/[^/<>:\"|?*]+)+/?$")
        return regex.matches(path)
    }
}
