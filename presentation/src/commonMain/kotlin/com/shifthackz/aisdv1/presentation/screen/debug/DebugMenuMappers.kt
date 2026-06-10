package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText

/**
 * Converts SDAI data with `mapToUi`.
 *
 * @author Dmitriy Moroz
 */
fun SchedulersToken.mapToUi(): UiText = when (this) {
    SchedulersToken.MAIN_THREAD -> "scheduler_main"
    SchedulersToken.IO_THREAD -> "scheduler_io"
    SchedulersToken.COMPUTATION -> "scheduler_computation"
    SchedulersToken.SINGLE_THREAD -> "scheduler_single_thread"
}.let {
    Localization.string(it)
}.asUiText()
