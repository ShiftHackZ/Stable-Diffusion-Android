package com.shifthackz.aisdv1.presentation.screen.backup

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.backup.CreateBackupUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect
import io.reactivex.rxjava3.kotlin.subscribeBy

class BackupViewModel(
    private val mainRouter: MainRouter,
    private val createBackupUseCase: CreateBackupUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<BackupState, BackupIntent, BackupEffect>() {

    override val initialState = BackupState()

    override fun processIntent(intent: BackupIntent) {
        when (intent) {
            BackupIntent.NavigateBack -> when (currentState.step) {
                BackupState.Step.SelectOperation -> mainRouter.navigateBack()
                BackupState.Step.ProcessBackup -> updateState {
                    it.copy(step = BackupState.Step.entries.first())
                }
            }

            BackupIntent.MainButtonClick -> when (currentState.step) {
                BackupState.Step.SelectOperation -> updateState {
                    it.copy(step = BackupState.Step.ProcessBackup)
                }

                BackupState.Step.ProcessBackup -> when (val op = currentState.operation) {
                    is BackupState.Operation.Create -> !createBackupUseCase(op.tokens)
                        .map(BackupEffect::SaveBackup)
                        .subscribeOnMainThread(schedulersProvider)
                        .subscribeBy(::errorLog, ::emitEffect)

                    is BackupState.Operation.Restore -> Unit
                    null -> Unit
                }
            }

            is BackupIntent.SelectOperation -> updateState {
                it.copy(
                    operation = when (intent.value) {
                        BackupState.Operation.Create::class.java.name -> {
                            BackupState.Operation.Create()
                        }
                        else -> BackupState.Operation.Restore()
                    },
                )
            }

            is BackupIntent.ToggleBackupEntry -> updateState { state ->
                when (state.operation) {
                    is BackupState.Operation.Create -> state.copy(
                        operation = state.operation.copy(
                            tokens = state.operation.tokens.map { (entry, selected) ->
                                if (intent.entry == entry) entry to intent.checked
                                else entry to selected
                            },
                        ),
                    )

                    is BackupState.Operation.Restore -> state
                    null -> state
                }
            }
        }
    }
}
