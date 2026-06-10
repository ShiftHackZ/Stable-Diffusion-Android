package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.core.mvi.MviIntent

sealed interface DonateIntent : MviIntent {

    data object NavigateBack : DonateIntent

    data object LaunchDonate : DonateIntent
}
