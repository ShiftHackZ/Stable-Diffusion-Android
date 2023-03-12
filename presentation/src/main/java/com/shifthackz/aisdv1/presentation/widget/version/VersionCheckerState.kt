package com.shifthackz.aisdv1.presentation.widget.version

import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.domain.usecase.version.CheckAppVersionUpdateUseCase

interface VersionCheckerState : MviState {
    object Idle : VersionCheckerState
    data class UpdatePopUp(val result: CheckAppVersionUpdateUseCase.Result.NewVersionAvailable) :
        VersionCheckerState
}
