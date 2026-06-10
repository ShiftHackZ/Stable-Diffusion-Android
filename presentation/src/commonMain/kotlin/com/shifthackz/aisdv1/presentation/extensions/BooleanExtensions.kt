package com.shifthackz.aisdv1.presentation.extensions

import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText

fun Boolean.mapToUi(): UiText = Localization
    .string(if (this) "yes" else "no")
    .asUiText()
