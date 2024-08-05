package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect

class WebUiViewModel(
    private val mainRouter: MainRouter,
    private val preferenceManager: PreferenceManager,
) : MviRxViewModel<WebUiState, WebUiIntent, EmptyEffect>() {

    override val initialState: WebUiState = WebUiState()

    init {
        updateState { state ->
            state.copy(
                loading = false,
                source = preferenceManager.source,
                url = when (preferenceManager.source) {
                    ServerSource.AUTOMATIC1111 -> preferenceManager.automatic1111ServerUrl
                    ServerSource.SWARM_UI -> preferenceManager.swarmUiServerUrl
                    else -> ""
                }
            )
        }
    }

    override fun processIntent(intent: WebUiIntent) {
        when (intent) {
            WebUiIntent.NavigateBack -> mainRouter.navigateBack()
        }
    }
}
