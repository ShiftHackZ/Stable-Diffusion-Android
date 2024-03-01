package com.shifthackz.aisdv1.presentation.modal.history

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.android.core.mvi.MviState

data class InputHistoryItemUi(
    val generationResult: AiGenerationResult,
    val bitmap: Bitmap,
)

object InputHistoryState : MviState
