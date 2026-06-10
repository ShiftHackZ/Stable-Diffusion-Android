package com.shifthackz.aisdv1.presentation.screen.donate

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.Supporter

/**
 * Carries `DonateState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class DonateState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `supporters` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val supporters: List<Supporter> = emptyList(),
) : MviState
