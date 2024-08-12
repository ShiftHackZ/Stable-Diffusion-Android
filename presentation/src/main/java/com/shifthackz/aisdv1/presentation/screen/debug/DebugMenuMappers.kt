package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

fun SchedulersToken.mapToUi(): UiText = when (this) {
    SchedulersToken.MAIN_THREAD -> LocalizationR.string.scheduler_main
    SchedulersToken.IO_THREAD -> LocalizationR.string.scheduler_io
    SchedulersToken.COMPUTATION -> LocalizationR.string.scheduler_computation
    SchedulersToken.SINGLE_THREAD -> LocalizationR.string.scheduler_single_thread
}.asUiText()
