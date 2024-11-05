package com.shifthackz.aisdv1.presentation.screen.report

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.android.core.mvi.MviState

data class ReportState(
    val loading: Boolean = true,
    val screenModal: Modal = Modal.None,
    val imageBitmap: Bitmap? = null,
    val imageBase64: String = "",
    val text: String = "",
    val reason: ReportReason = ReportReason.Other,
    val reportSent: Boolean = false,
) : MviState
