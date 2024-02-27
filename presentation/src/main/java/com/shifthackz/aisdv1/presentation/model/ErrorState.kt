package com.shifthackz.aisdv1.presentation.model

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviState

sealed interface ErrorState : MviState {

    data object None : ErrorState

    data object Generic : ErrorState

    data class WithMessage(val message: UiText) : ErrorState
}
