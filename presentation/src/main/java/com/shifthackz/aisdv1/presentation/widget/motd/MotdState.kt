package com.shifthackz.aisdv1.presentation.widget.motd

import com.shifthackz.aisdv1.core.ui.MviState

sealed interface MotdState : MviState {

    object Hidden : MotdState

    data class Content(
        val title: String,
        val subTitle: String,
    ) : MotdState
}
