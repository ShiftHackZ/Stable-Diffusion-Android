package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Carries `HomeState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class HomeState(
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
     * Exposes the `endpoint` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val endpoint: String = "",
    /**
     * Exposes the `error` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val error: String? = null,
) : MviState
