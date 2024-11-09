package com.shifthackz.aisdv1.presentation.screen.backup

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.backup.CreateBackupUseCase
import com.shifthackz.aisdv1.domain.usecase.backup.RestoreBackupUseCase
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect
import io.reactivex.rxjava3.kotlin.subscribeBy

class BackupViewModel(
    private val mainRouter: MainRouter,
    private val createBackupUseCase: CreateBackupUseCase,
    private val restoreBackupUseCase: RestoreBackupUseCase,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<BackupState, BackupIntent, BackupEffect>() {

    override val initialState = BackupState()

    override fun processIntent(intent: BackupIntent) {
        when (intent) {
            BackupIntent.NavigateBack -> {
                if (currentState.complete || currentState.step == BackupState.Step.SelectOperation) {
                    mainRouter.navigateBack()
                } else {
                    updateState {
                        it.copy(step = BackupState.Step.entries.first())
                    }
                }
            }

            BackupIntent.MainButtonClick -> if (currentState.complete) {
                mainRouter.navigateBack()
            } else when (currentState.step) {
                BackupState.Step.SelectOperation -> updateState {
                    it.copy(step = BackupState.Step.ProcessBackup)
                }

                BackupState.Step.ProcessBackup -> when (val op = currentState.operation) {
                    is BackupState.Operation.Create -> !createBackupUseCase(op.tokens)
                        .doOnSubscribe { updateState { it.copy(loading = true) } }
                        .map(BackupEffect::SaveBackup)
                        .subscribeOnMainThread(schedulersProvider)
                        .subscribeBy(::errorLog, ::emitEffect)

                    is BackupState.Operation.Restore -> currentState.backupToRestore
                        ?.second
                        ?.let(restoreBackupUseCase::invoke)
                        ?.doOnSubscribe { updateState { it.copy(loading = true) } }
                        ?.subscribeOnMainThread(schedulersProvider)
                        ?.subscribeBy(::errorLog) {
                            updateState { it.copy(loading = false, complete = true) }
                        }
                        ?.addToDisposable()

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

            BackupIntent.OnResult.Fail -> updateState {
                it.copy(
                    screenModal = Modal.Error("Error creating backup".asUiText()),
                    loading = false,
                )
            }

            BackupIntent.OnResult.Success -> updateState {
                it.copy(complete = true, loading = false)
            }

            BackupIntent.DismissModal -> updateState {
                it.copy(screenModal = Modal.None)
            }

            is BackupIntent.SelectRestore -> updateState {
                it.copy(backupToRestore = intent.path to intent.bytes)
            }
        }
    }
}
