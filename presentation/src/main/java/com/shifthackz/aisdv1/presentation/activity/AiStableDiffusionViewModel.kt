package com.shifthackz.aisdv1.presentation.activity

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyIntent
import com.shifthackz.android.core.mvi.EmptyState
import io.reactivex.rxjava3.kotlin.subscribeBy

class AiStableDiffusionViewModel(
    schedulersProvider: SchedulersProvider,
    mainRouter: MainRouter,
    drawerRouter: DrawerRouter,
    private val preferenceManager: PreferenceManager,
) : MviRxViewModel<EmptyState, EmptyIntent, NavigationEffect>() {

    override val initialState = EmptyState

    init {
        !mainRouter.observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda, ::emitEffect)

        !drawerRouter.observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda, ::emitEffect)
    }

    fun onStoragePermissionsGranted() {
        preferenceManager.saveToMediaStore = true
    }
}
