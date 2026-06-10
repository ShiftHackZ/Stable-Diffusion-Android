package com.shifthackz.aisdv1.presentation.screen.donate

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.Supporter

@Immutable
data class DonateState(
    val loading: Boolean = true,
    val supporters: List<Supporter> = emptyList(),
) : MviState
