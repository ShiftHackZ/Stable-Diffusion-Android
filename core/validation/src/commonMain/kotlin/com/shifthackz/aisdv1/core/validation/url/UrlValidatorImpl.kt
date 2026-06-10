package com.shifthackz.aisdv1.core.validation.url

import com.shifthackz.aisdv1.core.validation.ValidationResult

/**
 * Implements `UrlValidator` behavior in the SDAI validation layer.
 *
 * @author Dmitriy Moroz
 */
internal class UrlValidatorImpl : UrlValidator {

    /**
     * Executes the `invoke` step in the SDAI validation layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
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

    /**
     * Executes the `isPortValid` step in the SDAI validation layer.
     *
     * @param url remote URL used by the operation.
     * @return Result produced by `isPortValid`.
     * @author Dmitriy Moroz
     */
    private fun isPortValid(url: String): Boolean {
        val port = parseUrl(url)?.port ?: return true
        if (port.length > MAX_PORT_LENGTH) return true
        val portNumber = port.toIntOrNull() ?: return true
        return portNumber in MIN_PORT..MAX_PORT
    }

    /**
     * Executes the `isLocalhostUrl` step in the SDAI validation layer.
     *
     * @param url remote URL used by the operation.
     * @return Result produced by `isLocalhostUrl`.
     * @author Dmitriy Moroz
     */
    private fun isLocalhostUrl(url: String): Boolean {
        val host = parseUrl(url)?.host ?: return false
        return host.equals(LOCALHOST_ALIAS, true)
                || host.equals(LOCALHOST_IPV4, true)
                || host.equals(LOCALHOST_IPV6, true)
    }

    /**
     * Executes the `isUrlValid` step in the SDAI validation layer.
     *
     * @param url remote URL used by the operation.
     * @return Result produced by `isUrlValid`.
     * @author Dmitriy Moroz
     */
    private fun isUrlValid(url: String): Boolean {
        val parts = parseUrl(url) ?: return false
        return isHostValid(parts.host)
    }

    /**
     * Executes the `parseUrl` step in the SDAI validation layer.
     *
     * @param url remote URL used by the operation.
     * @return Result produced by `parseUrl`.
     * @author Dmitriy Moroz
     */
    private fun parseUrl(url: String): UrlParts? {
        val match = URL_PATTERN.matchEntire(url) ?: return null
        return UrlParts(
            host = match.groupValues[GROUP_HOST],
            port = match.groupValues[GROUP_PORT].takeIf(String::isNotEmpty),
        )
    }

    /**
     * Executes the `isHostValid` step in the SDAI validation layer.
     *
     * @param host host value consumed by the API.
     * @return Result produced by `isHostValid`.
     * @author Dmitriy Moroz
     */
    private fun isHostValid(host: String): Boolean = when {
        host.isBlank() -> false
        host.startsWith("[") && host.endsWith("]") -> IPV6_PATTERN.matches(host)
        host.all { it.isDigit() || it == '.' } -> isIpv4Valid(host)
        else -> DOMAIN_PATTERN.matches(host)
    }

    /**
     * Executes the `isIpv4Valid` step in the SDAI validation layer.
     *
     * @param host host value consumed by the API.
     * @return Result produced by `isIpv4Valid`.
     * @author Dmitriy Moroz
     */
    private fun isIpv4Valid(host: String): Boolean {
        val parts = host.split(".")
        return parts.size == IPV4_PARTS_COUNT && parts.all { part ->
            part.isNotEmpty() && part.toIntOrNull()?.let { it in IPV4_PART_RANGE } == true
        }
    }

    /**
     * Provides the `companion object` singleton used by the SDAI validation layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `URL_PATTERN` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private val URL_PATTERN = Regex("^(https?://)([^/?#:]+|\\[[^\\]]+])(?::([^/?#]*))?(?:[/?#].*)?$")
        /**
         * Exposes the `DOMAIN_PATTERN` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private val DOMAIN_PATTERN = Regex("^([A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+[A-Za-z]{2,}$")
        /**
         * Exposes the `IPV6_PATTERN` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private val IPV6_PATTERN = Regex("^\\[[0-9A-Fa-f:.]+]$")
        /**
         * Exposes the `IPV4_PART_RANGE` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private val IPV4_PART_RANGE = 0..255
        /**
         * Exposes the `IPV4_PARTS_COUNT` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val IPV4_PARTS_COUNT = 4
        /**
         * Exposes the `GROUP_HOST` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val GROUP_HOST = 2
        /**
         * Exposes the `GROUP_PORT` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val GROUP_PORT = 3
        /**
         * Exposes the `MIN_PORT` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val MIN_PORT = 1
        /**
         * Exposes the `MAX_PORT` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val MAX_PORT = 65535
        /**
         * Exposes the `MAX_PORT_LENGTH` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val MAX_PORT_LENGTH = 5
        /**
         * Exposes the `SCHEME_HTTPS` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val SCHEME_HTTPS = "https://"
        /**
         * Exposes the `SCHEME_HTTP` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val SCHEME_HTTP = "http://"
        /**
         * Exposes the `LOCALHOST_ALIAS` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val LOCALHOST_ALIAS = "localhost"
        /**
         * Exposes the `LOCALHOST_IPV4` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val LOCALHOST_IPV4 = "127.0.0.1"
        /**
         * Exposes the `LOCALHOST_IPV6` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        private const val LOCALHOST_IPV6 = "[::1]"
    }

    /**
     * Carries `UrlParts` data through the SDAI validation layer.
     *
     * @author Dmitriy Moroz
     */
    private data class UrlParts(
        /**
         * Exposes the `host` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        val host: String,
        /**
         * Exposes the `port` value used by the SDAI validation layer.
         *
         * @author Dmitriy Moroz
         */
        val port: String?,
    )
}
