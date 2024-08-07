package com.shifthackz.aisdv1.presentation.extensions

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

fun Boolean.mapToUi(): UiText = (if (this) LocalizationR.string.yes else LocalizationR.string.no).asUiText()
