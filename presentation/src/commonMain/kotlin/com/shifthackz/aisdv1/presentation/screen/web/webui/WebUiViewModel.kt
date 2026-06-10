package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.navigation.router.WebUiRouter

/**
 * Coordinates `WebUiViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class WebUiViewModel(
    dispatchersProvider: DispatchersProvider,
    preferenceManager: PreferenceManager,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: WebUiRouter,
) : BaseMviViewModel<WebUiState, WebUiIntent, EmptyEffect>(
    initialState = WebUiState(
        loading = false,
        source = preferenceManager.source,
        url = preferenceManager.webUiUrl(),
    ),
    effectDispatcher = dispatchersProvider.immediate,
) {

    override fun processIntent(intent: WebUiIntent) {
        when (intent) {
            WebUiIntent.NavigateBack -> router.navigateBack()
        }
    }
}

/**
 * Executes the `webUiUrl` step in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private fun PreferenceManager.webUiUrl(): String = when (source) {
    ServerSource.AUTOMATIC1111 -> automatic1111ServerUrl
    ServerSource.SWARM_UI -> swarmUiServerUrl
    else -> ""
}
