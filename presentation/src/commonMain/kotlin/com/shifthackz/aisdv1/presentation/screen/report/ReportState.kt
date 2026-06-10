package com.shifthackz.aisdv1.presentation.screen.report

import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.presentation.model.ErrorState

data class ReportState(
    val loading: Boolean = true,
    val error: ErrorState = ErrorState.None,
    val imageBase64: String = "",
    val text: String = "",
    val reason: ReportReason = ReportReason.Other,
    val reportSent: Boolean = false,
) : MviState
