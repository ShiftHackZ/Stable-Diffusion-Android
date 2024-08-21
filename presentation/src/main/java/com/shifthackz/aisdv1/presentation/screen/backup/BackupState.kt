package com.shifthackz.aisdv1.presentation.screen.backup

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.BackupEntryToken
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class BackupState(
    val step: Step = Step.SelectOperation,
    val operation: Operation? = null,
) : MviState {

    enum class Step {
        SelectOperation, ProcessBackup;
    }

    sealed interface Operation {
        data class Create(
            val tokens: List<Pair<BackupEntryToken, Boolean>> = BackupEntryToken.entries.map { it to false },
        ) : Operation

        data class Restore(
            val tokens: List<Pair<BackupEntryToken, Boolean>> = BackupEntryToken.entries.map { it to false },
        ) : Operation
    }
}
