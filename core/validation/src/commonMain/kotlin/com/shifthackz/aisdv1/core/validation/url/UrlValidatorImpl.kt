package com.shifthackz.aisdv1.core.validation.url

import com.shifthackz.aisdv1.core.validation.ValidationResult

internal class UrlValidatorImpl : UrlValidator {

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
        !isUrlValid(input) -> ValidationResult(
            isValid = false,
            validationError = UrlValidator.Error.Invalid,
        )
        else -> ValidationResult(isValid = true)
    }

    private fun isPortValid(url: String): Boolean {
        val port = parseUrl(url)?.port ?: return true
        if (port.length > MAX_PORT_LENGTH) return true
        val portNumber = port.toIntOrNull() ?: return true
        return portNumber in MIN_PORT..MAX_PORT
    }

    private fun isLocalhostUrl(url: String): Boolean {
        val host = parseUrl(url)?.host ?: return false
        return host.equals(LOCALHOST_ALIAS, true)
                || host.equals(LOCALHOST_IPV4, true)
                || host.equals(LOCALHOST_IPV6, true)
    }

    private fun isUrlValid(url: String): Boolean {
        val parts = parseUrl(url) ?: return false
        return isHostValid(parts.host)
    }

    private fun parseUrl(url: String): UrlParts? {
        val match = URL_PATTERN.matchEntire(url) ?: return null
        return UrlParts(
            host = match.groupValues[GROUP_HOST],
            port = match.groupValues[GROUP_PORT].takeIf(String::isNotEmpty),
        )
    }

    private fun isHostValid(host: String): Boolean = when {
        host.isBlank() -> false
        host.startsWith("[") && host.endsWith("]") -> IPV6_PATTERN.matches(host)
        host.all { it.isDigit() || it == '.' } -> isIpv4Valid(host)
        else -> DOMAIN_PATTERN.matches(host)
    }

    private fun isIpv4Valid(host: String): Boolean {
        val parts = host.split(".")
        return parts.size == IPV4_PARTS_COUNT && parts.all { part ->
            part.isNotEmpty() && part.toIntOrNull()?.let { it in IPV4_PART_RANGE } == true
        }
    }

    companion object {
        private val URL_PATTERN = Regex("^(https?://)([^/?#:]+|\\[[^\\]]+])(?::([^/?#]*))?(?:[/?#].*)?$")
        private val DOMAIN_PATTERN = Regex("^([A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+[A-Za-z]{2,}$")
        private val IPV6_PATTERN = Regex("^\\[[0-9A-Fa-f:.]+]$")
        private val IPV4_PART_RANGE = 0..255
        private const val IPV4_PARTS_COUNT = 4
        private const val GROUP_HOST = 2
        private const val GROUP_PORT = 3
        private const val MIN_PORT = 1
        private const val MAX_PORT = 65535
        private const val MAX_PORT_LENGTH = 5
        private const val SCHEME_HTTPS = "https://"
        private const val SCHEME_HTTP = "http://"
        private const val LOCALHOST_ALIAS = "localhost"
        private const val LOCALHOST_IPV4 = "127.0.0.1"
        private const val LOCALHOST_IPV6 = "[::1]"
    }

    private data class UrlParts(
        val host: String,
        val port: String?,
    )
}
