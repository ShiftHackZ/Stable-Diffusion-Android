package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.android.core.mvi.MviEffect

sealed interface DonateEffect : MviEffect {
    data class OpenUrl(val url: String) : DonateEffect
}
