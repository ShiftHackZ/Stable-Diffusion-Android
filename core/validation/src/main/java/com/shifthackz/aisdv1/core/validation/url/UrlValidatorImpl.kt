package com.shifthackz.aisdv1.core.validation.url

import android.util.Patterns
import android.webkit.URLUtil
import com.shifthackz.aisdv1.core.validation.ValidationResult

class UrlValidatorImpl : UrlValidator {

    override operator fun invoke(input: String?): ValidationResult<UrlValidator.Error> = when {
        input == null -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.Empty,
        )
        input.trim().isEmpty() -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.Empty
        )
        !input.startsWith(SCHEME_HTTP) && !input.startsWith(SCHEME_HTTPS) -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.BadScheme,
        )
        !URLUtil.isValidUrl(input) -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.Invalid,
        )
        !Patterns.WEB_URL.matcher(input).matches() -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.Invalid,
        )
        else -> ValidationResult(isValid = true)
    }

    companion object {
        private const val SCHEME_HTTPS = "https://"
        private const val SCHEME_HTTP = "http://"
    }
}
