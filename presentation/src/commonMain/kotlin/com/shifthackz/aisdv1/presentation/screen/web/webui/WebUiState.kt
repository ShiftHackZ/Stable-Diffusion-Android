package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Carries `WebUiState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class WebUiState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `source` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    /**
     * Exposes the `url` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val url: String = "",
) : MviState
