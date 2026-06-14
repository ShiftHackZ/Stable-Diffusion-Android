package com.shifthackz.aisdv1.presentation.screen.networkusage.model

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.presentation.model.UsageState

/**
 * State for the standalone network usage screen.
 *
 * @param usage Shared dashboard state with traffic buckets and loading flag.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class NetworkUsageState(
    /**
     * Shared usage dashboard state with traffic buckets and loading flag.
     *
     * @author Dmitriy Moroz
     */
    val usage: UsageState = UsageState(loading = true),
) : MviState
