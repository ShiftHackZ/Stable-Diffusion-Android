package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.url.UrlValidator

fun ValidationResult<UrlValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    val key = when (validationError as UrlValidator.Error) {
        UrlValidator.Error.BadScheme -> "error_invalid_scheme"
        UrlValidator.Error.BadPort -> "error_invalid_port"
        UrlValidator.Error.Empty -> "error_empty_url"
        UrlValidator.Error.Invalid -> "error_invalid_url"
        UrlValidator.Error.Localhost -> null
    } ?: return null
    return Localization.string(key).asUiText()
}
