package com.shifthackz.aisdv1.presentation.modal.download

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `DownloadDialogState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class DownloadDialogState(
    /**
     * Exposes the `sources` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sources: List<Pair<String, Boolean>> = emptyList(),
) : MviState {

    val selectedUrl: String
        get() = sources.find { (_, selected) -> selected }?.first ?: ""
}
