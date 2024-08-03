package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.presentation.R

fun ValidationResult<CommonStringValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    return when (validationError as CommonStringValidator.Error) {
        CommonStringValidator.Error.Empty -> R.string.error_empty_field
    }.asUiText()
}
