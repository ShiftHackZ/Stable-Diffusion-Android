package com.shifthackz.aisdv1.presentation.theme.global

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyIntent
import io.reactivex.rxjava3.kotlin.subscribeBy

class AiSdAppThemeViewModel(
    preferenceManager: PreferenceManager,
    dispatchersProvider: DispatchersProvider,
    schedulersProvider: SchedulersProvider,
    timeProvider: TimeProvider,
) : MviRxViewModel<AiSdAppThemeState, EmptyIntent, EmptyEffect>() {

    override val initialState = AiSdAppThemeState()

    override val effectDispatcher = dispatchersProvider.immediate

    init {
        !preferenceManager
            .observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda) { settings ->
                updateState { state ->
                    state.copy(
                        stateKey = timeProvider.currentTimeMillis(),
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
