package com.shifthackz.aisdv1.presentation.extensions

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.R

fun Boolean.mapToUi(): UiText = (if (this) R.string.yes else R.string.no).asUiText()
