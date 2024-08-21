package com.shifthackz.aisdv1.presentation.screen.backup.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.presentation.screen.backup.BackupIntent
import com.shifthackz.aisdv1.presentation.screen.backup.BackupState
import com.shifthackz.aisdv1.presentation.screen.backup.components.BackupEntriesComponent

@Composable
fun CreateBackupForm(
    state: BackupState,
    processIntent: (BackupIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        BackupEntriesComponent(
            tokens = (state.operation as BackupState.Operation.Create).tokens,
            onCheckedChange = { entry, checked ->
                processIntent(BackupIntent.ToggleBackupEntry(entry, checked))
            },
        )
    }
}
