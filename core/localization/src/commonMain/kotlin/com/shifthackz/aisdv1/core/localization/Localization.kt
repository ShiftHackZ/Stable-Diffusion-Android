package com.shifthackz.aisdv1.core.localization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Carries `LocalizationLanguage` data through the SDAI localization layer.
 *
 * @author Dmitriy Moroz
 */
data class LocalizationLanguage(
    /**
     * Exposes the `code` value used by the SDAI localization layer.
     *
     * @author Dmitriy Moroz
     */
    val code: String,
    /**
     * Exposes the `name` value used by the SDAI localization layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
)

/**
 * Provides the `Localization` singleton used by the SDAI localization layer.
 *
 * @author Dmitriy Moroz
 */
object Localization {

    /**
     * Exposes the `DEFAULT_LANGUAGE_CODE` value used by the SDAI localization layer.
     *
     * @author Dmitriy Moroz
     */
    const val DEFAULT_LANGUAGE_CODE = "en"

    /**
     * Exposes the `languageCodeState` value used by the SDAI localization layer.
     *
     * @author Dmitriy Moroz
     */
    private val languageCodeState = MutableStateFlow(supportedLanguageCode(platformLanguageCode()))

    /**
     * Exposes the `entries` value used by the SDAI localization layer.
     *
     * @author Dmitriy Moroz
     */
    val entries: List<LocalizationLanguage> = localizationLanguages

    /**
     * Exposes the `languageCodeFlow` value used by the SDAI localization layer.
     *
     * @author Dmitriy Moroz
     */
    val languageCodeFlow: StateFlow<String> = languageCodeState.asStateFlow()

    /**
     * Executes the `currentLanguageCode` step in the SDAI localization layer.
     *
     * @return Result produced by `currentLanguageCode`.
     * @author Dmitriy Moroz
     */
    fun currentLanguageCode(): String = languageCodeState.value

    /**
     * Executes the `setLanguageCode` step in the SDAI localization layer.
     *
     * @param code code value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun setLanguageCode(code: String?) {
        languageCodeState.value = code?.let(::supportedLanguageCode)
            ?: supportedLanguageCode(platformLanguageCode())
    }

    /**
     * Executes the `strings` step in the SDAI localization layer.
     *
     * @param languageCode BCP-47 language code handled by the platform layer.
     * @return Result produced by `strings`.
     * @author Dmitriy Moroz
     */
    fun strings(languageCode: String = currentLanguageCode()): Map<String, String> =
        localizationCatalog.getValue(supportedLanguageCode(languageCode))

    /**
     * Executes the `string` step in the SDAI localization layer.
     *
     * @param key key value consumed by the API.
     * @param args args value consumed by the API.
     * @param languageCode BCP-47 language code handled by the platform layer.
     * @author Dmitriy Moroz
     */
    fun string(
        key: String,
        vararg args: Any?,
        languageCode: String = currentLanguageCode(),
    ): String = strings(languageCode)
        .getOrElse(key) { localizationCatalog.getValue(DEFAULT_LANGUAGE_CODE)[key] ?: key }
        .format(args)

    /**
     * Loads SDAI data through `getCountryFlagDrawableResId`.
     *
     * @param code code value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun getCountryFlagDrawableResId(code: String): Int? = countryFlagDrawableResId(code)

    /**
     * Executes the `supportedLanguageCode` step in the SDAI localization layer.
     *
     * @param code code value consumed by the API.
     * @return Result produced by `supportedLanguageCode`.
     * @author Dmitriy Moroz
     */
    private fun supportedLanguageCode(code: String?): String {
        val normalized = code
            ?.lowercase()
            ?.replace('_', '-')
            ?.substringBefore('-')
            ?.takeIf(String::isNotBlank)

        return normalized
            ?.takeIf(localizationCatalog::containsKey)
            ?: DEFAULT_LANGUAGE_CODE
    }

    /**
     * Executes the `format` step in the SDAI localization layer.
     *
     * @param args args value consumed by the API.
     * @return Result produced by `format`.
     * @author Dmitriy Moroz
     */
    private fun String.format(args: Array<out Any?>): String {
        var result = this
        args.forEachIndexed { index, arg ->
            result = result.replace("%${index + 1}\$s", arg.toString())
        }
        args.forEach { arg ->
            result = result.replaceFirst("%s", arg.toString())
        }
        return result
    }
}

/**
 * Executes the `countryFlagDrawableResId` step in the SDAI localization layer.
 *
 * @param code code value consumed by the API.
 * @return Result produced by `countryFlagDrawableResId`.
 * @author Dmitriy Moroz
 */
internal expect fun countryFlagDrawableResId(code: String): Int?

/**
 * Executes the `platformLanguageCode` step in the SDAI localization layer.
 *
 * @return Result produced by `platformLanguageCode`.
 * @author Dmitriy Moroz
 */
internal expect fun platformLanguageCode(): String?
