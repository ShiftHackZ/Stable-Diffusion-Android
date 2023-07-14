package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.horde.HordeApiKeyValidator
import com.shifthackz.aisdv1.presentation.R

fun ValidationResult<HordeApiKeyValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    return when (validationError as HordeApiKeyValidator.Error) {
        HordeApiKeyValidator.Error.Empty -> R.string.error_empty_api_key
    }.asUiText()
}
