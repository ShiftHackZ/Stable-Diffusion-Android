package com.shifthackz.aisdv1.presentation.screen.backup

import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect

class BackupViewModel(
    private val mainRouter: MainRouter,
) : MviRxViewModel<BackupState, BackupIntent, EmptyEffect>() {

    override val initialState = BackupState()

    override fun processIntent(intent: BackupIntent) {
        when (intent) {
            BackupIntent.NavigateBack -> mainRouter.navigateBack()
        }
    }
}
