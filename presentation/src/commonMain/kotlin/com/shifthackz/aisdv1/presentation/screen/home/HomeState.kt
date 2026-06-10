package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.ServerSource

@Immutable
data class HomeState(
    val loading: Boolean = true,
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    val endpoint: String = "",
    val error: String? = null,
) : MviState
