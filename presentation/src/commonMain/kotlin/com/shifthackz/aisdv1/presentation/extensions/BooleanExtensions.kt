package com.shifthackz.aisdv1.presentation.extensions

import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText

/**
 * Converts SDAI data with `mapToUi`.
 *
 * @return Result produced by `mapToUi`.
 * @author Dmitriy Moroz
 */
fun Boolean.mapToUi(): UiText = Localization
    .string(if (this) "yes" else "no")
    .asUiText()
