package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.EmptyState
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.debug.DebugInsertBadBase64UseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class DebugMenuViewModel(
    private val debugInsertBadBase64UseCase: DebugInsertBadBase64UseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmptyState, EmptyEffect>() {

    override val emptyState = EmptyState

    fun insertBadBase64() = !debugInsertBadBase64UseCase()
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog)
}
