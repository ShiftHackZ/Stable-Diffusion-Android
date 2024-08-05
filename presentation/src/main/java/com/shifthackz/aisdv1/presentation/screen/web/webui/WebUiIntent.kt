package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.android.core.mvi.MviIntent

sealed interface WebUiIntent : MviIntent {
    data object NavigateBack : WebUiIntent
}
