package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.android.core.mvi.MviState

data class WebUiState(
    val loading: Boolean = true,
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    val url: String = "",
) : MviState
