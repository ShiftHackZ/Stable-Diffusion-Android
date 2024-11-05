package com.shifthackz.aisdv1.presentation.screen.report

import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.android.core.mvi.MviIntent

sealed interface ReportIntent : MviIntent {
    data class UpdateText(val text: String) : ReportIntent
    data class UpdateReason(val reason: ReportReason) : ReportIntent
    data object Submit : ReportIntent
    data object NavigateBack : ReportIntent
    data object DismissError : ReportIntent
}
