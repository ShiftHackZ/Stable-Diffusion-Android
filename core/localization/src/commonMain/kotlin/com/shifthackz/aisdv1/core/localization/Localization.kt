package com.shifthackz.aisdv1.core.localization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LocalizationLanguage(
    val code: String,
    val name: String,
)

object Localization {

    const val DEFAULT_LANGUAGE_CODE = "en"

    private val languageCodeState = MutableStateFlow(supportedLanguageCode(platformLanguageCode()))

    val entries: List<LocalizationLanguage> = localizationLanguages

    val languageCodeFlow: StateFlow<String> = languageCodeState.asStateFlow()

    fun currentLanguageCode(): String = languageCodeState.value

    fun setLanguageCode(code: String?) {
        languageCodeState.value = code?.let(::supportedLanguageCode)
            ?: supportedLanguageCode(platformLanguageCode())
    }

    fun strings(languageCode: String = currentLanguageCode()): Map<String, String> =
        localizationCatalog.getValue(supportedLanguageCode(languageCode))

    fun string(
        key: String,
        vararg args: Any?,
        languageCode: String = currentLanguageCode(),
    ): String = strings(languageCode)
        .getOrElse(key) { localizationCatalog.getValue(DEFAULT_LANGUAGE_CODE)[key] ?: key }
        .format(args)

    fun getCountryFlagDrawableResId(code: String): Int? = countryFlagDrawableResId(code)

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

internal expect fun countryFlagDrawableResId(code: String): Int?

internal expect fun platformLanguageCode(): String?
