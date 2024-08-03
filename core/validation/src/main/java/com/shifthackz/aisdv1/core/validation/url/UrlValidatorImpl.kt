package com.shifthackz.aisdv1.core.validation.url

import android.util.Patterns
import android.webkit.URLUtil
import com.shifthackz.aisdv1.core.validation.ValidationResult
import java.net.URI
import java.util.regex.Pattern

internal class UrlValidatorImpl(
    private val webUrlPattern: Pattern = Patterns.WEB_URL,
) : UrlValidator {

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
        !isPortValid(input) -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.BadPort,
        )
        isLocalhostUrl(input) -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.Localhost,
        )
        !URLUtil.isValidUrl(input) -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.Invalid,
        )
        !webUrlPattern.matcher(input).matches() -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.Invalid,
        )
        else -> ValidationResult(isValid = true)
    }

    private fun isPortValid(url: String): Boolean = try {
        val uri = URI(url)
        val port = uri.port
        port in 1..65535 || port == -1
    } catch (e: Exception) {
        false
    }

    private fun isLocalhostUrl(url: String): Boolean = try {
        val uri = URI(url)
        val host = uri.host
        host.equals(LOCALHOST_ALIAS, true)
                || host.equals(LOCALHOST_IPV4, true)
                || host.equals(LOCALHOST_IPV6, true)
    } catch (e: Exception) {
        false
    }

    companion object {
        private const val SCHEME_HTTPS = "https://"
        private const val SCHEME_HTTP = "http://"
        private const val LOCALHOST_ALIAS = "localhost"
        private const val LOCALHOST_IPV4 = "127.0.0.1"
        private const val LOCALHOST_IPV6 = "[::1]"
    }
}
