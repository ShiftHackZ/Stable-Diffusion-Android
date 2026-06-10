package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.ServerSource

data class WebUiState(
    val loading: Boolean = true,
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    val url: String = "",
) : MviState
