package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

fun ValidationResult<UrlValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    return when (validationError as UrlValidator.Error) {
        UrlValidator.Error.BadScheme -> LocalizationR.string.error_invalid_scheme
        UrlValidator.Error.BadPort -> LocalizationR.string.error_invalid_port
        UrlValidator.Error.Empty -> LocalizationR.string.error_empty_url
        UrlValidator.Error.Invalid -> LocalizationR.string.error_invalid_url
        UrlValidator.Error.Localhost -> null
    }?.asUiText()
}
