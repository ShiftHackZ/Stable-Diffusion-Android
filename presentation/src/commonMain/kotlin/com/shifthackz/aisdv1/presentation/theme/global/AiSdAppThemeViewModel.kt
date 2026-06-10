package com.shifthackz.aisdv1.presentation.theme.global

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.core.mvi.EmptyIntent
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import kotlinx.coroutines.flow.catch

/**
 * Coordinates `AiSdAppThemeViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class AiSdAppThemeViewModel(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    preferenceManager: PreferenceManager,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<AiSdAppThemeState, EmptyIntent, EmptyEffect>(
    initialState = AiSdAppThemeState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        launch(dispatchersProvider.immediate) {
            preferenceManager
                .observe()
                .catch { t -> onError(t) }
                .collect { settings ->
                    updateState { state ->
                        state.copy(
                            stateKey = state.stateKey + 1L,
                            systemColorPalette = settings.designUseSystemColorPalette,
                            systemDarkTheme = settings.designUseSystemDarkTheme,
                            darkTheme = settings.designDarkTheme,
                            colorToken = ColorToken.parse(settings.designColorToken),
                            darkThemeToken = DarkThemeToken.parse(settings.designDarkThemeToken),
                        )
                    }
                }
        }
    }

    override fun processIntent(intent: EmptyIntent) = Unit
}
