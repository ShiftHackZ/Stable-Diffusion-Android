package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.core.mvi.MviEffect

/**
 * Defines the `DonateEffect` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DonateEffect : MviEffect {
    /**
     * Carries `OpenUrl` data through the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    data class OpenUrl(val url: String) : DonateEffect
}
