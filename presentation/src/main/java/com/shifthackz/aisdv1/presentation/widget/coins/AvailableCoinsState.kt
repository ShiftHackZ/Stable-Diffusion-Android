package com.shifthackz.aisdv1.presentation.widget.coins

import com.shifthackz.aisdv1.core.ui.MviState

interface AvailableCoinsState : MviState {
    object Hidden : AvailableCoinsState
    data class Content(val value: Int) : AvailableCoinsState
}
