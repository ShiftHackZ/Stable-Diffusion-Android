package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator

/**
 * Executes the `function` step in the SDAI presentation layer.
 *
 * @return Result produced by `function`.
 * @author Dmitriy Moroz
 */
fun ValidationResult<CommonStringValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    return when (validationError as CommonStringValidator.Error) {
        CommonStringValidator.Error.Empty -> Localization.string("error_empty_field")
    }.asUiText()
}
